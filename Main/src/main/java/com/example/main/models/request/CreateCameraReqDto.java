package com.example.main.models.request;

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
public class CreateCameraReqDto {

    private String label;
    private Long zoneId;

    public static Camera mapToEntity(CreateCameraReqDto reqDto){
        return Camera
                .builder()
                .label(reqDto.label)
                .build();
    }
}
