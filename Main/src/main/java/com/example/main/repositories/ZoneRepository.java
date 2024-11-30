package com.example.main.repositories;

import com.example.main.models.entities.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Page<Zone> findAll(Specification<Zone> spec, Pageable pageable);
}