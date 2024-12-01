package com.example.main.services;

import com.example.main.models.entities.File;
import com.example.main.models.entities.Person;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface FileService {
    @Transactional
    public void deleteFile(File file);

    public MultipartFile convertBufferedImageToMultipartFile(BufferedImage bufferedImage, String fileName) throws IOException;
    @Transactional
    public File uploadFile(MultipartFile file, Person person);
}
