package com.hospital.hospital_backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.Recipe;
import com.hospital.hospital_backend.repositories.RecipeRepository;
import com.hospital.hospital_backend.payloads.medication.MedicationPayload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    public boolean createOrUpdateRecipe(Patient patient, List<MedicationPayload> medications) {
        try {
            String content = objectMapper.writeValueAsString(medications);
            Optional<Recipe> existing = recipeRepository.findByPatientId(patient.getId());
            Recipe recipe = existing.orElseGet(() -> new Recipe(null, patient, content));
            recipe.setContent(content);
            recipe.setPatient(patient); // asigură asociația corectă
            recipeRepository.save(recipe);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }


}
