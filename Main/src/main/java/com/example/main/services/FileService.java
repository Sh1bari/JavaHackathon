package com.example.main.services;

import com.example.main.models.entities.File;
import com.example.main.models.entities.Person;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface FileService {

    public void deleteFile(File file);

    public MultipartFile convertBufferedImageToMultipartFile(BufferedImage bufferedImage, String fileName) throws IOException;

    public File uploadFile(MultipartFile file, Person person);
}
