package com.hospital.hospital_backend.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.Recipe;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.repositories.RecipeRepository;
import com.hospital.hospital_backend.services.MedicationService;
import com.hospital.hospital_backend.services.PatientService;
import com.hospital.hospital_backend.services.RecipeService;
import com.hospital.hospital_backend.services.UserService;
import com.hospital.hospital_backend.payloads.medication.MedicationPayload;
import com.hospital.hospital_backend.payloads.medication.RecipePayload;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/recipe/v1")
public class RecipeController {
    private final MedicationService medicationService;
    private final RecipeService recipeService;
    private final PatientService patientService;
    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final FirebaseAuth firebaseAuth;

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PutMapping("/createRecipe/{cnp}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> createRecipeForPatient(@PathVariable String cnp, @RequestBody List<MedicationPayload> medications) {
        try {
            Patient patient = patientService.findPatientByCnp(cnp);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pacientul cu acest CNP nu a fost găsit.");
            }

            boolean approved = medicationService.requestMedications(medications);
            if (!approved) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stocul medicamentelor este insuficient.");
            }

            boolean recipeSaved = recipeService.createOrUpdateRecipe(patient, medications);
            if (!recipeSaved) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nu s-a putut salva rețeta.");
            }

            return ResponseEntity.ok("Rețeta a fost creată sau suprascrisă, iar stocurile au fost actualizate cu succes.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare internă: " + e.getMessage());
        }
    }



    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getReteta/{cnp}")
    public ResponseEntity<?> getRetetaByCnp(@PathVariable String cnp) {
        try {
            Patient patient = patientService.findPatientByCnp(cnp);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pacientul cu CNP-ul dat nu a fost găsit.");
            }

            Optional<Recipe> retete = recipeRepository.findByPatientId(patient.getId());
            if (retete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pacientul nu are rețete înregistrate.");
            }

            return ResponseEntity.ok(new RecipePayload(retete.get().getContent()));


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Eroare internă: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getMyRecipe")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> getMyRecipe() {
        try {
            User patientAsUser = userService
                    .findByFirebaseId(SecurityContextHolder.getContext()
                            .getAuthentication().getName());
            if (patientAsUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                        "patient or the user cannot be found.");
            }
            // Obține pacientul asociat
            Patient patient = patientService.getPatientById(patientAsUser.getPatMedAdminId());
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pacientul nu a fost găsit.");
            }

            Optional<Recipe> reteta = recipeRepository.findByPatientId(patient.getId());
            if (reteta.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nu există rețetă salvată.");
            }

            return ResponseEntity.ok(new RecipePayload(reteta.get().getContent()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare internă: " + e.getMessage());
        }
    }
}
