package com.example.main.services;

import com.example.main.models.entities.Person;
import com.example.main.models.request.ChangePersonDtoReq;
import com.example.main.models.request.CreatePersonDtoReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface PersonService {
    @Transactional
    public Person createPerson(CreatePersonDtoReq dto);
    @Transactional
    public Page<Person> getPersons(Specification<Person> spec, Pageable pageable);
    @Transactional
    public Person findById(UUID id);
    @Transactional
    Person changePerson(ChangePersonDtoReq dto);
}
