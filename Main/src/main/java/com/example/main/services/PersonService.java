package com.example.main.services;

import com.example.main.models.entities.Person;
import com.example.main.models.request.ChangePersonDtoReq;
import com.example.main.models.request.CreatePersonDtoReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface PersonService {

    public Person createPerson(CreatePersonDtoReq dto);

    public Page<Person> getPersons(Specification<Person> spec, Pageable pageable);

    public Person findById(UUID id);

    Person changePerson(ChangePersonDtoReq dto);
}
