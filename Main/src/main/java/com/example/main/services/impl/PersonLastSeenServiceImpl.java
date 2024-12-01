package com.example.main.services.impl;

import com.example.main.models.entities.*;
import com.example.main.repositories.PersonLastSeenRepository;
import com.example.main.services.CameraService;
import com.example.main.services.PersonLastSeenService;
import com.example.main.services.PersonService;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonLastSeenServiceImpl implements PersonLastSeenService {

    private final PersonLastSeenRepository repository;
    private final CameraService cameraService;
    private final PersonService personService;

    @Transactional
    public void save(PersonLastSeen personLastSeen){
        repository.save(personLastSeen);
    }

    @Override
    @Transactional
    public void createPersonLastSeen(String cameraUrl, UUID personId, Instant time) {
        OffsetDateTime offsetDateTime = time.atOffset(ZoneOffset.UTC);
        Camera camera = cameraService.findByUrl(cameraUrl);
        save(PersonLastSeen.builder()
                .lastSeen(offsetDateTime)
                .camera(camera)
                .zone(camera.getZone())
                .person(personService.findById(personId))
                .build());
    }
}
