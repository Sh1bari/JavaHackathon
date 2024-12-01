package com.example.main.services.impl;

import com.example.main.models.dto.KafkaResponseDto;
import com.example.main.services.PersonLastSeenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final PersonLastSeenService personLastSeenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topic.send-ids}", groupId = "main-service-group")
    public void processReceivedIds(String payload) {
        try {
            KafkaResponseDto responseDto = objectMapper.readValue(payload, KafkaResponseDto.class);

            log.info("Получены данные из Kafka: CameraId={}, Timestamp={}, IDs={}",
                    responseDto.getCameraId(),
                    responseDto.getTimestamp(),
                    responseDto.getIds());
            handleReceivedData(responseDto);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения из Kafka: {}", e.getMessage());
        }
    }

    private void handleReceivedData(KafkaResponseDto responseDto) {
        log.info("Обработка данных для камеры {} с ID: {}", responseDto.getCameraId(), responseDto.getIds());
        responseDto.getIds().forEach(uuid -> personLastSeenService.createPersonLastSeen(responseDto.getCameraId(), uuid, responseDto.getTimestamp()));
    }
}
