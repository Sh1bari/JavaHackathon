package com.example.main.repositories;

import com.example.main.models.entities.PersonLastSeen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface PersonLastSeenRepository extends JpaRepository<PersonLastSeen, Long> {
    List<PersonLastSeen> findAllByLastSeenBetween(OffsetDateTime start, OffsetDateTime end);
}