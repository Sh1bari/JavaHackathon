package com.example.main.services.impl;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.File;
import com.example.main.models.entities.Person;
import com.example.main.repositories.FileRepository;
import com.example.main.services.FileService;
import com.example.main.utils.CustomMultipartFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final FileRepository fileRepository;

    @Value("${minio.defaultBucket}")
    private String defaultBucket;

    // Метод загрузки файла в MinIO
    @Override
    public File uploadFile(MultipartFile file, Person person) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        long size = file.getSize();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(defaultBucket)
                            .object(fileName)
                            .stream(inputStream, size, -1)
                            .contentType(file.getContentType())
                            .build()
            );

            File fileEntity = File.builder()
                    .name(fileName)
                    .fileExtension(extension)
                    .size(size)
                    .bucket(defaultBucket)
                    .person(person)
                    .path(getFileUrl(defaultBucket, fileName))
                    .build();
            return fileRepository.save(fileEntity);
        } catch (Exception e) {
            throw new GeneralException(404, e.toString());
        }
    }

    public MultipartFile convertBufferedImageToMultipartFile(BufferedImage bufferedImage, String fileName) throws IOException {
        // Конвертируем BufferedImage в массив байтов
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream); // Здесь формат можно заменить на "png", если нужно
        byte[] imageBytes = outputStream.toByteArray();

        // Создаём кастомный MultipartFile
        return new CustomMultipartFile(
                imageBytes,
                fileName + ".jpg",
                "image/jpeg"
        );
    }


    public String getFileUrl(String bucketName, String fileName) {
        return "http://localhost:9001/" + bucketName + "/" + fileName;
    }

    @Transactional
    public void deleteFile(File file){
        fileRepository.delete(file);
    }
}
