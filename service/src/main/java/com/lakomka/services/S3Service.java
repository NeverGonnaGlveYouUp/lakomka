package com.lakomka.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    /**
     * Загрузка файла в S3
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        log.info("File uploaded successfully: {}", fileName);
        return fileName;
    }

    /**
     * Скачивание файла из S3
     */
    public byte[] downloadFile(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }

    /**
     * Удаление файла из S3
     */
    public boolean deleteFile(String fileName) {
        try {

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Throwable t) {
            log.error("Error:Delete", t);
            return false;
        }
        log.info("File deleted successfully: {}", fileName);
        return true;
    }

    /**
     * Получение списка файлов в бакете
     */
    public List<String> listFiles() {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

        return listObjectsResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    /**
     * Проверка существования файла
     */
    public boolean fileExists(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    /**
     * Генерация уникального имени файла
     */
    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    /**
     * Создание бакета если не существует
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket alredy existed: {}", bucketName);
            return true;
        } catch (NoSuchBucketException e) {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket created: {}", bucketName);
            return true;
        } catch (Throwable t) {
            log.error("Error:Create bucket", t);
            return false;
        }
    }

    /**
     * Переименование файла
     *
     * @param oldKey The current key/name of the file
     * @param newKey The new key/name for the file
     * @return true if successful, false otherwise
     */
    public boolean renameFile(String oldKey, String newKey) {
        try {
            // Copy the object to the new key
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(oldKey)
                    .destinationBucket(bucketName)
                    .destinationKey(newKey)
                    .build();

            CopyObjectResponse copyResponse = s3Client.copyObject(copyRequest);

            // If copy was successful, delete the original
            if (copyResponse.sdkHttpResponse().isSuccessful()) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(oldKey)
                        .build();

                s3Client.deleteObject(deleteRequest);
                return true;
            }

            return false;

        } catch (Throwable t) {
            log.error("Error:Rename", t);
            return false;
        }
    }
}

