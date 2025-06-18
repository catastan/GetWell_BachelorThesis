package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "medications")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private MedicationCategory category;

    public Medication(String name, int stock, @NotNull MedicationCategory category) {
        this.name = name;
        this.stock = stock;
        this.category = category;
    }
}
