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
public class Person {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    private String name;
    private String middleName;
    private String surname;

    private Status status = Status.ACTIVE;

    @OneToOne(mappedBy = "person", orphanRemoval = true)
    private File file;
}
