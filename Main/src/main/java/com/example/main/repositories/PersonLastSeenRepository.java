package com.example.main.repositories;

import com.example.main.models.entities.PersonLastSeen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonLastSeenRepository extends JpaRepository<PersonLastSeen, Long> {
}