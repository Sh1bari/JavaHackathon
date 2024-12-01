package com.example.mockservice.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFaceDto {
    private UUID id;
    private String message;
    private String status;

    public static ResponseFaceDtoBuilder basicMapping(String message, String status){
        return ResponseFaceDto.builder()
                .message(message)
                .status(status);
    }

    public static ResponseFaceDto mapFromEntityError(String message, String status){
        return basicMapping(message, status)
                .build();
    }

    public static ResponseFaceDto mapFromEntityWithId(String message, String status, UUID id){
        return basicMapping(message, status)
                .id(id)
                .build();
    }
}
