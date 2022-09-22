package com.dolphin.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.apache.tika.Tika;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class AmazonS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;


    /* 이미지 업로드 기능 */
    @Transactional
    public List<String> upload(List<MultipartFile> multipartFiles) throws IOException {
        /* 이미지 파일인지 여부 검증 */
        isImage(multipartFiles);

        List<String> filenameList = new ArrayList<>();
        multipartFiles.forEach(file -> {
            /* 고유한 파일 이름 생성 */
            String filename = createFilename(file.getOriginalFilename());
            /* 이미지 리사이징을 위한 확장자명 추출 */
            String fileFormatName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            MultipartFile resizedFile = resizeImage(filename, fileFormatName, file, 750);

            /* objectMetaData에 파라미터로 들어온 파일의 타입 , 크기를 할당 */
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(resizedFile.getSize());
            objectMetadata.setContentType(file.getContentType());
            System.out.println(resizedFile.getSize());

            /* S3객체의 putObject 메서드로 파일 업로드 */
            try (InputStream inputStream = resizedFile.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, filename, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            filenameList.add(filename);
        });
        return filenameList;
    }


    /* 이미지 삭제 기능 */
    @Transactional
    public void deleteFile(String filename) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
        s3.deleteObject(bucket, filename);
    }


    /* 고유한 파일 이름 생성 */
    public String createFilename(String filename) {
        return UUID.randomUUID().toString().concat(filename);
    }


    /* 이미지 파일인지 확인하는 메소드 */
    private void isImage(List<MultipartFile> multipartFile) throws IOException {

        /* tika를 이용해 파일 MIME 타입 체크 */
        /* 파일명에 .jpg 식으로 붙는 확장자는 없앨 수도 있고 조작도 가능하므로 MIME 타입을 체크 */
        Tika tika = new Tika();
        for (int i = 0; i < multipartFile.size(); i++) {
            String mimeType = tika.detect(multipartFile.get(i).getInputStream());
            /* MIME타입이 이미지가 아니면 exception 발생 */
            if (!mimeType.startsWith("image/")) {
                throw new IllegalStateException("이미지 파일이 아닙니다");
            }
        }
    }


    /* 이미지 리사이징 */
    MultipartFile resizeImage(String filename, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            /* MultipartFile을 BufferedImage로 변환 */
            BufferedImage image = ImageIO.read(originalImage.getInputStream());
            /* newWidth : newHeight = originWidth : originHeight */
            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            /* origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함 */
            if (originWidth < targetWidth)
                return originalImage;

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormatName, baos);
            baos.flush();

            return new MockMultipartFile(filename, baos.toByteArray());

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }

}
