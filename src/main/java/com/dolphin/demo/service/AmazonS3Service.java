package com.dolphin.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dolphin.demo.exception.CustomException;
import com.dolphin.demo.exception.ErrorCode;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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


    // 이미지 업로드 기능(다중 업로드)
    @Transactional
    public List<String> upload(List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles == null) {
            return Collections.emptyList();
        }

        // 이미지 파일인지 여부 검증
        isImage(multipartFiles);

        List<String> imageUrlList = new ArrayList<>();
        multipartFiles.forEach(file -> {
            /* 고유한 파일 이름 생성 */
            String fileFormatName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            String filename = createFilename(fileFormatName);

            MultipartFile resizedFile = resizeImage(filename, fileFormatName, file, 800);

            // objectMetaData에 파라미터로 들어온 파일의 타입 , 크기를 할당
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(resizedFile.getSize());
            objectMetadata.setContentType(file.getContentType());

            // S3객체의 putObject 메서드로 파일 업로드
            try (InputStream inputStream = resizedFile.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, filename, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.UPLOAD_FAIL);
            }

            imageUrlList.add(amazonS3Client.getUrl(bucket, filename).toString());
        });
        return imageUrlList;
    }

    // 이미지 업로드 기능(단일 업로드)
    public String upload(MultipartFile multipartFile) throws IOException {

        if(multipartFile == null) {
            return null;
        }

        // 이미지 파일인지 여부 검증
        isImage(multipartFile);

        String fileFormatName = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
        String filename = createFilename(fileFormatName);

        MultipartFile resizedFile = resizeImage(filename, fileFormatName, multipartFile, 800);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = resizedFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, filename, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.UPLOAD_FAIL);
        }
        return amazonS3Client.getUrl(bucket, filename).toString();
    }


    // 이미지 삭제 기능
    @Transactional
    public void deleteFile(String filename) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
        s3.deleteObject(bucket, filename);
    }


    // 고유한 파일 이름 생성
    public String createFilename(String fileFormatName) {
        return UUID.randomUUID().toString().concat("." + fileFormatName);
    }


    // 이미지 파일인지 확인하는 메소드(다중 업로드)
    private void isImage(List<MultipartFile> multipartFile) throws IOException {

        /* tika를 이용해 파일 MIME 타입 체크
          파일명에 .jpg 식으로 붙는 확장자는 없앨 수도 있고 조작도 가능하므로 MIME 타입을 체크 */
        Tika tika = new Tika();
        for (int i = 0; i < multipartFile.size(); i++) {
            String mimeType = tika.detect(multipartFile.get(i).getInputStream());
            /* MIME타입이 이미지가 아니면 exception 발생 */
            if (!mimeType.startsWith("image/")) {
                throw new CustomException(ErrorCode.BAD_REQUEST_IMAGE);
            }
        }
    }

    // 이미지 파일인지 확인하는 메소드(단일 업로드)
    private void isImage(MultipartFile multipartFile) throws IOException {

        Tika tika = new Tika();
        String mimeType = tika.detect(multipartFile.getInputStream());

        if (!mimeType.startsWith("image/")) {
            throw new CustomException(ErrorCode.BAD_REQUEST_IMAGE);
        }
    }


    // 이미지 리사이징
    MultipartFile resizeImage(String filename, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            // MultipartFile -> BufferedImage Convert
            BufferedImage image = ImageIO.read(originalImage.getInputStream());
            // newWidth : newHeight = originWidth : originHeight
            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            // origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함
            if (originWidth < targetWidth)
                return originalImage;

            MarvinImage imageMarvin = new MarvinImage(image);
            // 기존 이미지 파일(imageMarvin)을 리사이징
            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);


            // BufferedImage를 MultipartFile로 변환
            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormatName, baos);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(filename, baos.toByteArray());
            baos.flush();

            return mockMultipartFile;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }
}