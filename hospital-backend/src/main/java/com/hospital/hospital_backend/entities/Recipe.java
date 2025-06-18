package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", unique = true, nullable = false)
    private Patient patient;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
