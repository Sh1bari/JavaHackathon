package com.example.main.services;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Person;
import com.example.main.models.entities.Zone;
import com.example.main.repositories.ZoneRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepo;

    @Transactional(readOnly = true)
    public Zone findById(Long id) {
        return zoneRepo.findById(id)
                .orElseThrow(()->new GeneralException(404, "Zone not found"));
    }
    @Transactional
    public Zone save(Zone zone) {
        return zoneRepo.save(zone);
    }
}
