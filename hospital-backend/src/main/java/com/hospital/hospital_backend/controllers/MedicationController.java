package com.hospital.hospital_backend.controllers;


import com.hospital.hospital_backend.entities.Medication;
import com.hospital.hospital_backend.entities.MedicationCategory;
import com.hospital.hospital_backend.services.MedicationCategoryService;
import com.hospital.hospital_backend.services.MedicationService;
import com.hospital.hospital_backend.services.RecipeService;
import com.hospital.hospital_backend.payloads.medication.AddMedicationPayload;
import com.hospital.hospital_backend.payloads.medication.UpdateStockMedicationPayload;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/medication/v1")
public class MedicationController {
    private final MedicationService medicationService;
    private final MedicationCategoryService medicationCategoryService;
    private final RecipeService recipeService;

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getMedication")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMedication(@RequestParam String medicationName) {
        return ResponseEntity.ok(medicationService.getMedicationsContainingName(medicationName));
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/listAllCategories")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(medicationCategoryService.getAllCategories());
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getMedicationsByCategory")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMedicationsByCategory(@RequestParam String category) {
        List<MedicationService.MedicationDTO> medicationDTOList =
                medicationCategoryService.getAllMedicationWithCategory(category).stream().map(
                        medicationDTO -> new MedicationService.MedicationDTO(medicationDTO.getId(), medicationDTO.getName(),
                                medicationDTO.getStock(), category)
                ).toList();
        return ResponseEntity.ok(medicationDTOList);
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PutMapping("/updateStockMedication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateStockMedication(@RequestBody UpdateStockMedicationPayload updatePayload) {
        Boolean updated = medicationService.updateStock(updatePayload);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medication not found");
        }
        return ResponseEntity.ok("Stock updated successfully");
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/addMedication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addMedication(@RequestBody AddMedicationPayload medication) {
        Boolean categoryEntityExists = medicationCategoryService.existsCategory(medication.getCategory());
        if (!categoryEntityExists) {
            medicationCategoryService.createCategory(medication.getCategory());
        }
        MedicationCategory medicationCategory = medicationCategoryService.getCategory(medication.getCategory());
        if (medicationCategory == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found and could not be created");
        }
        Boolean savedMedication = medicationService.saveMedication(medication, medicationCategory);
        if (!savedMedication) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Medication could not be created");
        }
        Medication med = medicationService.getMedicationByName(medication.getName());
        if (med == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Although medication was shown as created, it could not be found, so it's a internal server error due to database unhealthy state");
        }
        medicationCategoryService.addMedicationToCategory(medication.getCategory(), med);
        return ResponseEntity.ok("Medication added successfully");
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @DeleteMapping("/deleteMedication/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMedication(@PathVariable Long id) {
        Medication medication = medicationService.getMedicationById(id);
        if (medication == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medication not found");
        }
        medicationCategoryService.deleteMedicationFromCategory(medication.getCategory().getName(), medication);
        Boolean deleted = medicationService.deleteMedication(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medication not found");
        }
        return ResponseEntity.ok("Medication deleted successfully");
    }
}
