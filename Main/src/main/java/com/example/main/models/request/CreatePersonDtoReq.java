package com.example.main.models.request;

import com.example.main.models.entities.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePersonDtoReq {

    private String name;
    private String middleName;
    private String surname;

    private MultipartFile photo;

    public static Person mapToEntity(CreatePersonDtoReq dtoReq){
        return Person.builder()
                .name(dtoReq.name)
                .middleName(dtoReq.middleName)
                .surname(dtoReq.surname)
                .build();
    }
}
