package com.example.photoprocessing.services;

import com.example.photoprocessing.config.MockServiceClient;
import com.example.photoprocessing.models.dto.KafkaRequestDto;
import com.example.photoprocessing.models.dto.ResponseFaceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class KafkaConsumerService {

    private final MinioService minioService;
    private final MockServiceClient mockServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaConsumerService(MinioService minioService,
                                MockServiceClient mockServiceClient,
                                KafkaProducerService kafkaProducerService) {
        this.minioService = minioService;
        this.mockServiceClient = mockServiceClient;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.receive-data}", groupId = "kafka-service-group")
    public void processData(String payload) {
        try {
            KafkaRequestDto requestDto = objectMapper.readValue(payload, KafkaRequestDto.class);

            log.info("Получены данные: CameraId={}, Bucket={}, Name={}, Timestamp={}",
                    requestDto.getCameraId(),
                    requestDto.getBucket(),
                    requestDto.getName(),
                    requestDto.getTimestamp());

            MultipartFile file = minioService.getFile(requestDto.getBucket(), requestDto.getName());
            List<UUID> ids = mockServiceClient.matchFaces(file)
                    .stream()
                    .map(ResponseFaceDto::getId)
                    .toList();

            kafkaProducerService.sendResponseToKafka(requestDto.getCameraId(), requestDto.getTimestamp(), ids);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: {}", e.getMessage());
        }
    }
}
