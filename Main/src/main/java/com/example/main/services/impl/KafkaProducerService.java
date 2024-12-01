package com.example.main.services.impl;

import com.example.main.models.dto.KafkaRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.topic.receive-data}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDataToKafka(String cameraId, String bucket, String name, Instant timestamp) {
        try {
            KafkaRequestDto requestDto = new KafkaRequestDto();
            requestDto.setCameraId(cameraId);
            requestDto.setBucket(bucket);
            requestDto.setName(name);
            requestDto.setTimestamp(timestamp);

            String payload = objectMapper.writeValueAsString(requestDto);
            kafkaTemplate.send(topicName, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации данных для Kafka", e);
        }
    }
}
