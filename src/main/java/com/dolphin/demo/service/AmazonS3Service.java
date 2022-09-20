package com.dolphin.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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
    public List<String> upload(List<MultipartFile> multipartFiles) {

        List<String> filenameList = new ArrayList<>();
        multipartFiles.forEach(file -> {
            /* 고유한 파일 이름 생성 */
            String filename = createFilename(file.getOriginalFilename());
            /* objectMetaData에 파라미터로 들어온 파일의 타입 , 크기를 할당 */
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            /* S3객체의 putObject 메서드로 파일 업로드 */
            try(InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, filename, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }catch(IOException e) {
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

}
