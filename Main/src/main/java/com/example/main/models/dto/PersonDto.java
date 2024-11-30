package com.example.main.models.dto;

import com.example.main.models.entities.File;
import com.example.main.models.entities.Person;
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
public class PersonDto {

    private String id;
    private String name;
    private String middleName;
    private String surname;
    private String status;

    private List<String> photoUrls;

    public static PersonDtoBuilder basicMapping(Person person){
        return PersonDto.builder()
                .id(person.getName())
                .status(person.getStatus().toString())
                .name(person.getName())
                .middleName(person.getMiddleName())
                .surname(person.getSurname());
    }

    public static PersonDto mapFromEntityWithPhoto(Person person){
        return basicMapping(person)
//                .photoUrls(person
//                        .getFi
//                        .stream()
//                        .map(File::getPath)
//                        .collect(Collectors.toList()))
                .build();
    }

    public static PersonDto mapFromEntity(Person person){
        return basicMapping(person)
                .build();
    }
}
