package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.MedicationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationCategoryRepository extends JpaRepository<MedicationCategory, Long> {
    Optional<MedicationCategory> findByName(String name);
}
