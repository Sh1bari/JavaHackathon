package com.example.main.repositories;

import com.example.main.models.entities.Camera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    Page<Camera> findAll(Specification<Camera> spec, Pageable pageable);

    Optional<Camera> findByUrl(String url);
}