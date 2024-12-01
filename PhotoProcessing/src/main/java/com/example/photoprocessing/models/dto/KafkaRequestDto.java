package com.example.photoprocessing.models.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class KafkaRequestDto {
    private String cameraId;
    private String bucket;
    private String name;
    private Instant timestamp;

    // Геттеры и сеттеры
}