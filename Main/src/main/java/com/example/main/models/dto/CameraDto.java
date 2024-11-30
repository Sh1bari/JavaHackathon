package com.example.main.models.dto;

import com.example.main.models.entities.Camera;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CameraDto {

    private String id;
    private String label;
    private String zoneId;
    private String status;

    public static CameraDtoBuilder basicMapping(Camera camera){
        return CameraDto
                .builder()
                .id(camera.getId().toString());
    }

    public static CameraDto mapFromEntitySimplified(Camera camera){
        return basicMapping(camera)
                .build();
    }

    public static CameraDto mapFromEntity(Camera camera){
        return basicMapping(camera)
                .label(camera.getLabel())
                .zoneId(camera.getZone().getId().toString())
                .status(camera.getStatus().toString())
                .build();
    }
}
