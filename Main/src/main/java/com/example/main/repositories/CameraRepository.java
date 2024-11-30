package com.example.main.repositories;

import com.example.main.models.entities.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraRepository extends JpaRepository<Camera, Long> {
}