package com.example.main.services;

import com.example.main.models.entities.Zone;
import com.example.main.models.request.CreateZoneReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ZoneService {

    public Zone findById(Long id);

    public Page<Zone> getZones(Specification<Zone> spec, Pageable pageable);

    public Zone createZone(CreateZoneReqDto dto);

    public Zone deleteZone(Long id);
}
