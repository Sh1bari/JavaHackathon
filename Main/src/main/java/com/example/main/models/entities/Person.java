package com.example.main.models.entities;

import com.example.main.models.enums.Status;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private String middleName;
    private String surname;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToOne(mappedBy = "person", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private File file;
}
