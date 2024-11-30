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
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String label;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;
}
