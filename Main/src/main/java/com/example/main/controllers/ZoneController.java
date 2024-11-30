package com.example.main.controllers;

import com.example.main.models.dto.ZoneDto;
import com.example.main.models.entities.Zone;
import com.example.main.models.enums.Status;
import com.example.main.models.request.CreateZoneReqDto;
import com.example.main.services.ZoneService;
import com.example.main.specifications.ZoneSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/zones")
    public ResponseEntity<Page<ZoneDto>> getZones(
            @RequestParam(required = false)String label,
            @RequestParam(required = false)Status status,
            @PageableDefault Pageable pageable,
            @RequestParam boolean cameraIncluded
    ){
        Specification<Zone> spec = Specification.where(
                ZoneSpecifications.hasStatus(status)
                .and(ZoneSpecifications.hasLabel(label))
        );
        Page<Zone> zones = zoneService.getZones(spec, pageable);
        Page<ZoneDto> zoneDtos = cameraIncluded ?
                zones.map(ZoneDto::mapFromEntityWithCameras) :
                zones.map(ZoneDto::mapFromEntity);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(zoneDtos);
    }

    @GetMapping("/zones/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable Long id,
                                           @RequestParam boolean cameraIncluded){
        ZoneDto res = cameraIncluded ?
                ZoneDto.mapFromEntityWithCameras(zoneService.findById(id)) :
                ZoneDto.mapFromEntity(zoneService.findById(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping("/zones")
    public ResponseEntity<ZoneDto> createZone(@RequestParam CreateZoneReqDto dto){
        ZoneDto res = ZoneDto.mapFromEntity(zoneService.createZone(dto));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @DeleteMapping("/zones/{id}")
    public ResponseEntity<ZoneDto> deleteZone(@PathVariable Long id){
        ZoneDto res = ZoneDto.mapFromEntity(zoneService.deleteZone(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
}
