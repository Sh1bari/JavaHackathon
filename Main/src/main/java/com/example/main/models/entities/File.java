package com.example.main.models.entities;

import com.example.main.models.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    private String bucket;
    private String name;

    @Column(unique = true)
    private String path;

    private Long size;

    private String fileExtension;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;
}
