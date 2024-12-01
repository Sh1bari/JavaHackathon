package com.example.main.controllers;

import com.example.main.models.dto.PersonDto;
import com.example.main.models.entities.Person;
import com.example.main.models.enums.Status;
import com.example.main.models.request.ChangePersonDtoReq;
import com.example.main.models.request.CreatePersonDtoReq;
import com.example.main.services.PersonService;
import com.example.main.specifications.PersonSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/persons")
    public ResponseEntity<Page<PersonDto>> getPersons(
            @RequestParam(required = false)String name,
            @RequestParam(required = false)String middleName,
            @RequestParam(required = false)String surname,
            @RequestParam(required = false)Status status,
            @PageableDefault Pageable pageable,
            @RequestParam boolean photoIncluded) {
        Specification<Person> spec = Specification.where(
                PersonSpecifications.hasStatus(status)
                .and(PersonSpecifications.hasName(name))
                .and(PersonSpecifications.hasMiddleName(middleName))
                .and(PersonSpecifications.hasSurname(surname))
        );
        Page<Person> persons = personService.getPersons(spec, pageable);
        Page<PersonDto> personDtos = photoIncluded ?
                persons.map(PersonDto::mapFromEntityWithPhoto) :
                persons.map(PersonDto::mapFromEntity);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(personDtos);
    }

    @GetMapping(value = "/persons/{id}")
    public ResponseEntity<PersonDto> getPersonById(@PathVariable UUID id,
                                                   @RequestParam boolean photoIncluded){
        PersonDto res = photoIncluded ?
                PersonDto.mapFromEntityWithPhoto(personService.findById(id)) :
                PersonDto.mapFromEntity(personService.findById(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping(value = "/persons",  consumes = {"multipart/form-data"})
    public ResponseEntity<PersonDto> createPerson(@RequestParam String name,
                                                  @RequestParam String middleName,
                                                  @RequestParam String surname,
                                                  @RequestPart("photo") MultipartFile photo) {
        CreatePersonDtoReq dto = new CreatePersonDtoReq();
        dto.setName(name);
        dto.setMiddleName(middleName);
        dto.setSurname(surname);
        dto.setPhoto(photo);
        PersonDto res = PersonDto.mapFromEntityWithPhoto(personService.createPerson(dto));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PutMapping(value = "/persons",  consumes = {"multipart/form-data"})
    public ResponseEntity<PersonDto> changePersonPhoto(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String middleName,
                                                       @RequestParam(required = false) String surname,
                                                       @RequestPart(name = "photo", required = false) MultipartFile photo) {
        ChangePersonDtoReq dto = new ChangePersonDtoReq();
        dto.setName(name);
        dto.setMiddleName(middleName);
        dto.setSurname(surname);
        dto.setPhoto(photo);
        PersonDto res = PersonDto.mapFromEntityWithPhoto(personService.changePerson(dto));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
}
