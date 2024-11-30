package com.example.main.services;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Person;
import com.example.main.models.request.CreatePersonDtoReq;
import com.example.main.repositories.PersonRepository;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepo;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Person findById(Long id) {
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
        return save(person);
    }
}
