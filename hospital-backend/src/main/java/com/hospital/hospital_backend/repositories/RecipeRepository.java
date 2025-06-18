package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByPatientId(Long patientId);
}
