package com.example.main.models.dto;

import com.example.main.models.entities.Zone;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZoneDto {
    private String id;
    private String label;
    private List<CameraDto> cameras;
    private String status;

    public static ZoneDtoBuilder basicMapping(Zone zone){
        return ZoneDto.builder()
                .id(zone.getId().toString())
                .label(zone.getLabel())
                .status(zone.getStatus().toString());
    }

    public static ZoneDto mapFromEntity(Zone zone){
        return basicMapping(zone)
                .build();
    }

    public static ZoneDto mapFromEntityWithCameras(Zone zone){
        if (zone.getCameras().isEmpty()) {
            return mapFromEntity(zone);
        } else {
            return basicMapping(zone)
                    .cameras(zone.getCameras()
                            .stream()
                            .map(CameraDto::mapFromEntitySimplified)
                            .collect(Collectors.toList()))
                    .build();
        }
    }
}
