package com.example.main.services.impl;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Person;
import com.example.main.models.request.ChangePersonDtoReq;
import com.example.main.models.request.CreatePersonDtoReq;
import com.example.main.repositories.PersonRepository;
import com.example.main.services.FaceService;
import com.example.main.services.FileService;
import com.example.main.services.PersonService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepo;
    private final FileService fileService;
    private final FaceService faceService;

    @Override
    @Transactional(readOnly = true)
    public Person findById(UUID id) {
        return personRepo.findById(id)
                .orElseThrow(()->new GeneralException(404, "Person not found"));
    }

    @Transactional(readOnly = true)
    public Page<Person> getPersons(Specification<Person> spec, Pageable pageable) {
        return personRepo.findAll(spec, pageable);
    }

    @Transactional
    public Person save(Person person) {
        return personRepo.save(person);
    }

    @Transactional
    public Person createPerson(CreatePersonDtoReq dto) {
        Person person = CreatePersonDtoReq.mapToEntity(dto);
        person.setFile(fileService.uploadFile(dto.getPhoto(), person));
        save(person);
        try {
            faceService.addFace(person.getId(), dto.getPhoto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return person;
    }

    @Override
    @Transactional
    public Person changePerson(ChangePersonDtoReq dto) {
        Person person = findById(dto.getId());
        person.setName(dto.getName());
        person.setSurname(dto.getSurname());
        person.setMiddleName(dto.getMiddleName());
        if (dto.getPhoto() != null){
            fileService.deleteFile(person.getFile());
            person.setFile(fileService.uploadFile(dto.getPhoto(), person));
            try {
                faceService.addFace(person.getId(), dto.getPhoto());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return save(person);
    }
}
