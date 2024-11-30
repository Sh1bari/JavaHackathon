package com.example.main.repositories;

import com.example.main.models.entities.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Page<Person> findAll(Specification<Person> spec, Pageable pageable);
}