package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medication_categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicationCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Medication> medications;

    public MedicationCategory(String name) {
        this.name = name;
        this.medications = new ArrayList<>();
    }
}
