package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.PastDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PastDiagnosisRepository extends JpaRepository<PastDiagnosis, Long> {
    List<PastDiagnosis> findAllByPatientId(Long patientId);
}
