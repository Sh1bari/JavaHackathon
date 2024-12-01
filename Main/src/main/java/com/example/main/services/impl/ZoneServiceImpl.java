package com.example.main.services.impl;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Zone;
import com.example.main.models.enums.Status;
import com.example.main.models.request.CreateZoneReqDto;
import com.example.main.repositories.ZoneRepository;
import com.example.main.services.ZoneService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {
    private final ZoneRepository zoneRepo;

    @Override
    @Transactional(readOnly = true)
    public Zone findById(Long id) {
        return zoneRepo.findById(id)
                .orElseThrow(()->new GeneralException(404, "Zone not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Zone> getZones(Specification<Zone> spec, Pageable pageable) {
        return zoneRepo.findAll(spec, pageable);
    }

    @Transactional
    public Zone save(Zone zone) {
        return zoneRepo.save(zone);
    }

    @Transactional
    public Zone createZone(CreateZoneReqDto dto) {
        Zone zone = CreateZoneReqDto.mapToEntity(dto);
        return save(zone);
    }

    @Override
    @Transactional
    public Zone deleteZone(Long id) {
        Zone zone = findById(id);
        if (zone.getStatus() == Status.DELETED){
            throw new GeneralException(400, "Zone is already deleted");
        }
        zone.setStatus(Status.DELETED);
        return save(zone);
    }
}
