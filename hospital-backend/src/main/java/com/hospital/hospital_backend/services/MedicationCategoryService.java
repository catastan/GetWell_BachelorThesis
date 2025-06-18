package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.Medication;
import com.hospital.hospital_backend.entities.MedicationCategory;
import com.hospital.hospital_backend.repositories.MedicationCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MedicationCategoryService {
    private final MedicationCategoryRepository medicationCategoryRepository;

    public List<String> getAllCategories() {
        return medicationCategoryRepository.findAll().stream().map(MedicationCategory::getName).toList();
    }

    public List<Medication> getAllMedicationWithCategory(String category) {
        MedicationCategory medicationCategory = medicationCategoryRepository.findByName(category).orElse(null);
        if (medicationCategory == null) {
            return new ArrayList<>();
        }
        return medicationCategory.getMedications();
    }

    public Boolean existsCategory(String category) {
        return medicationCategoryRepository.findByName(category).isPresent();
    }

    public void createCategory(String category) {
        MedicationCategory medicationCategory = new MedicationCategory(category);
        medicationCategoryRepository.save(medicationCategory);
    }

    public MedicationCategory getCategory(String category) {
        return medicationCategoryRepository.findByName(category).orElse(null);
    }

    public void addMedicationToCategory(String category, Medication medication) {
        MedicationCategory medicationCategory = getCategory(category);
        if (medicationCategory == null) {
            return;
        }
        medicationCategory.getMedications().add(medication);
        medicationCategoryRepository.save(medicationCategory);
    }

    public void deleteMedicationFromCategory(String category, Medication medication) {
        MedicationCategory medicationCategory = getCategory(category);
        if (medicationCategory == null) {
            return;
        }
        medicationCategory.getMedications().removeIf(a -> a.getName().equals(medication.getName()));
    }
}
