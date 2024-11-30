package com.example.main.repositories;

import com.example.main.models.entities.PersonInZoneDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonInZoneDurationRepository extends JpaRepository<PersonInZoneDuration, Long> {
}