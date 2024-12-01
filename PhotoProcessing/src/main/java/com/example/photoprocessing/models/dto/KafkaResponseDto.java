package com.example.photoprocessing.models.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class KafkaResponseDto {
    private String cameraId;
    private Instant timestamp;
    private List<UUID> ids;

    // Геттеры и сеттеры
}
