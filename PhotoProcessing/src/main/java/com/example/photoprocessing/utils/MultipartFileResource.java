package com.example.photoprocessing.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileResource extends InputStreamResource {

    private final String filename;

    public MultipartFileResource(MultipartFile file) throws IOException {
        super(file.getInputStream());
        this.filename = file.getOriginalFilename();
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
