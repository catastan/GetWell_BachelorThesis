package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @Column(name = "cnp")
    private String cnp;

    @Column(name = "actual_state_of_health")
    private String actualStateOfHealth;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<PastDiagnosis> pastDiagnoses;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_history_id", referencedColumnName = "id")
    private MedicalHistory medicalHistory;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments;


    public Patient(String cnp) {
        this.cnp = cnp;
        appointments = new ArrayList<>();
        pastDiagnoses = new ArrayList<>();
    }

}
