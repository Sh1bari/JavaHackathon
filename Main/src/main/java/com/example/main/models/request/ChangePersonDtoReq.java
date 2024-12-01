package com.example.main.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePersonDtoReq {

    private UUID id;
    private String name;
    private String middleName;
    private String surname;

    private MultipartFile photo;
}
