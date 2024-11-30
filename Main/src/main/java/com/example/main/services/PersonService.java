package com.example.main.services;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Person;
import com.example.main.repositories.PersonRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepo;

    @Transactional(readOnly = true)
    public Person findById(Long id) {
        return personRepo.findById(id)
                .orElseThrow(()->new GeneralException(404, "Person not found"));
    }
    @Transactional
    public Person save(Person person) {
        return personRepo.save(person);
    }
}
