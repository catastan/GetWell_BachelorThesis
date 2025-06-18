package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "past_diagnosis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PastDiagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "diagnosis", nullable = false)
    private String diagnosis;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public PastDiagnosis(String diagnosis, LocalDate date, Patient patient) {
        this.diagnosis = diagnosis;
        this.date = date;
        this.patient = patient;
    }
}
