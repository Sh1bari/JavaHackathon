//package com.example.mockservice.models.dto;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.UUID;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ResponseFaceDto {
//    private UUID id;
//    private String message;
//    private String status;
//
//    public static ResponseFaceDtoBuilder basicMapping(String message){
//        return ResponseFaceDto.builder()
//                .message(message)
//    }
//
//    public static ResponseFaceDto mapFromEntity()
//}
