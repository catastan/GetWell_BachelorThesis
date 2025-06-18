package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findAllByNameContainsIgnoreCase(String subName);

    Optional<Medication> findByName(String name);

    @Query("SELECT m FROM Medication m WHERE m.id = :id OR LOWER(m.name) = LOWER(:name)")
    Optional<Medication> findByIdOrName(@Param("id") Long id, @Param("name") String name);
}
