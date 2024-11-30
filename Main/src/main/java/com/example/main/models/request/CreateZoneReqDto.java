package com.example.main.models.request;

import com.example.main.models.entities.Zone;
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
public class CreateZoneReqDto {
    private String label;

    public static Zone mapToEntity(CreateZoneReqDto dto) {
        return Zone.builder()
                .label(dto.label)
                .build();
    }
}
