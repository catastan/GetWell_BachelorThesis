package com.hospital.hospital_backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.payloads.management.AddMedicalInfoPayload;
import com.hospital.hospital_backend.payloads.management.MedicalDiagnosisPayload;
import com.hospital.hospital_backend.payloads.management.StateOfHealthPayload;
import com.hospital.hospital_backend.payloads.management.UserIdentityPayload;
import com.hospital.hospital_backend.services.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("api/patientManagement/v1")
@AllArgsConstructor
@EnableMethodSecurity
public class PatientManagementController {

    private final AuthService authService;
    private final UserService userService;
    private final PatientService patientService;
    private final FirebaseAuth firebaseAuth;
    private final MedicalHistoryService medicalHistoryService;
    private final PastDiagnosisService pastDiagnosisService;
    private final UserPatientService userPatientService;

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/getStateOfHealth")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getStateOfHealth(@RequestBody UserIdentityPayload userIdentityPayload) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Patient patient = userPatientService.findPatientByIdentityPayload(userIdentityPayload);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(patient.getActualStateOfHealth());
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PutMapping("/updateStateOfHealth")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> updateStateOfHealth(@RequestBody StateOfHealthPayload stateOfHealthPayload) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Patient patient = userPatientService
                .findPatientByIdentityPayload(new UserIdentityPayload(stateOfHealthPayload
                        .getUsername(), stateOfHealthPayload.getEmail(),
                        stateOfHealthPayload.getCnp()));
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        boolean updated = patientService.updateStateOfHealth(patient,
                stateOfHealthPayload.getStateOfHealth());
        return updated ? ResponseEntity.status(HttpStatus.OK)
                .body("Updated successfully.") : ResponseEntity
                .badRequest().build();
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping(value = "/addMedicalInfo", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<String> addMedicalInfo(@ModelAttribute AddMedicalInfoPayload addMedicalInfoPayload) {
        System.out.println(addMedicalInfoPayload.getUsername());
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Patient patient = userPatientService
                .findPatientByIdentityPayload(new UserIdentityPayload(addMedicalInfoPayload
                        .getUsername(), addMedicalInfoPayload.getEmail(),
                        addMedicalInfoPayload.getCnp()));
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        return medicalHistoryService.addFilesToMedicalHistory(patient, addMedicalInfoPayload);
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping(value = "/getMedicalInfo")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<byte[]> seeMedicalInfo(@RequestBody UserIdentityPayload userIdentityPayload) throws IOException {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User unauthorized".getBytes());
        }

        Patient patient = userPatientService.findPatientByIdentityPayload(userIdentityPayload);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found".getBytes());
        }

        return patientService.getMedicalHistory(patient);
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/addDiagnosis")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addMedicalDiagnosis(@RequestBody MedicalDiagnosisPayload medicalDiagnosisPayload) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Patient patient = userPatientService
                .findPatientByIdentityPayload(new UserIdentityPayload(medicalDiagnosisPayload
                        .getUsername(), medicalDiagnosisPayload.getEmail(),
                        medicalDiagnosisPayload.getCnp()));
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        pastDiagnosisService.savePastDiagnosis(patient, medicalDiagnosisPayload.getDiagnosis(),
                medicalDiagnosisPayload.getDiagnosisDate());
        return ResponseEntity.ok("Diagnosis added successfully.");
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/getDiagnoses")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getMedicalDiagnosis(@RequestBody UserIdentityPayload userIdentityPayload) throws JsonProcessingException {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(403).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Patient patient = userPatientService
                .findPatientByIdentityPayload(userIdentityPayload);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        return ResponseEntity.ok((new ObjectMapper()).writeValueAsString(patientService.getMedicalDiagnoses(patient)));
    }
}
