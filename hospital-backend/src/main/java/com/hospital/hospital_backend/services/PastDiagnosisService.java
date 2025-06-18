package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.PastDiagnosis;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.repositories.PastDiagnosisRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Service
public class PastDiagnosisService {
    private PastDiagnosisRepository pastDiagnosisRepository;
    private PatientService patientService;

    public void savePastDiagnosis(Patient patient, String diagnosis, LocalDate date) {
        patient.getPastDiagnoses().add(new PastDiagnosis(diagnosis, date, patient));
        patientService.savePatient(patient);
    }
}
