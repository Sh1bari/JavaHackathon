package com.example.main.repositories;

import com.example.main.models.entities.PersonInZoneDuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonInZoneDurationRepository extends JpaRepository<PersonInZoneDuration, Long> {
    Optional<PersonInZoneDuration> findByPersonIdAndZoneId(UUID personId, Long zoneId);
}