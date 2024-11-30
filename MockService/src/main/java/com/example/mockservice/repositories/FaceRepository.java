package com.example.mockservice.repositories;

import com.example.mockservice.models.entities.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FaceRepository extends JpaRepository<Face, UUID> {
}