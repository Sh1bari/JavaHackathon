package com.example.main.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface FaceService {

    public void addFace(UUID personId, MultipartFile photo) throws IOException;
}
