package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "MedicalHistory")
@NoArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "folder_name", length = 128)
    private String folderName;

    @OneToOne(mappedBy = "medicalHistory")
    private Patient patient;

    public MedicalHistory(String folderName, Patient patient) {
        this.folderName = folderName;
        this.patient = patient;
    }
}
