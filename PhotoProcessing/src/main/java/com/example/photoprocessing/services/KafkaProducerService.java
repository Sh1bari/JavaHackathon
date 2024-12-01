package com.example.photoprocessing.services;

import com.example.photoprocessing.models.dto.KafkaResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.topic.send-ids}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendResponseToKafka(String cameraId, Instant timestamp, List<UUID> ids) {
        try {
            KafkaResponseDto responseDto = new KafkaResponseDto();
            responseDto.setCameraId(cameraId);
            responseDto.setTimestamp(timestamp);
            responseDto.setIds(ids);

            String payload = objectMapper.writeValueAsString(responseDto);
            kafkaTemplate.send(topicName, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации данных для Kafka", e);
        }
    }
}
