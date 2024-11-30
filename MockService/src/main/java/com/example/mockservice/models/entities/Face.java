package com.example.mockservice.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Face {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] faceData;
}
