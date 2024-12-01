package com.example.photoprocessing.services;

import com.example.photoprocessing.utils.CustomMultipartFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public MultipartFile getFile(String bucket, String name) {
        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(name)
                    .build());
            return new CustomMultipartFile(inputStream.readAllBytes(), name, "image/jpeg");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла из MinIO", e);
        }
    }
}
