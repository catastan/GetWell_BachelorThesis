package com.hospital.hospital_backend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.hospital_backend.entities.Appointment;
import com.hospital.hospital_backend.entities.Doctor;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.services.*;
import com.hospital.hospital_backend.payloads.appointment.AppointmentDatePayload;
import com.hospital.hospital_backend.payloads.appointment.AppointmentPayload;
import com.hospital.hospital_backend.payloads.management.UserIdentityPayload;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/appointment/v1")
public class AppointmentController {
    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserPatientService userPatientService;
    private final AppointmentService appointmentService;

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getAppointments")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getAppointments() {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Doctor doctor = doctorService.getDoctorById(doctorAsUser.getPatMedAdminId());

        return ResponseEntity.ok(doctorService.getAppointments(doctor));
    }
    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<?> getAppointmentsForPatient() {
        User patientAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (patientAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "patient or the user cannot be found.");
        }
        Patient patient = patientService.getPatientById(patientAsUser.getPatMedAdminId());
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patient));
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/addAppointment")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addAppointment(@RequestBody AppointmentPayload appointment) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Doctor doctor = doctorService.getDoctorById(doctorAsUser.getPatMedAdminId());
        Patient patient = userPatientService
                .findPatientByIdentityPayload(new UserIdentityPayload(appointment
                        .getUsername(), appointment.getEmail(),
                        appointment.getCnp()));
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.");
        }
        Long idAppointment = appointmentService.addNewAppointment(doctor, patient, appointment);
        if (idAppointment != -1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(idAppointment);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict for this appointment");
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/getAppointmentsPerDay")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getAppointmentsPerDay(@RequestBody AppointmentDatePayload appointmentDatePayload) throws JsonProcessingException {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Doctor doctor = doctorService.getDoctorById(doctorAsUser.getPatMedAdminId());

        return ResponseEntity.ok(doctorService.getAppointmentsPerDay(doctor, appointmentDatePayload));
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @DeleteMapping("/deleteAppointment")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @Transactional
    public ResponseEntity<?> deleteAppointment(@RequestBody AppointmentDatePayload appointment) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }
        Doctor doctor = doctorService.getDoctorById(doctorAsUser.getPatMedAdminId());
        Appointment appointment1 = doctorService.deleteAppointment(doctor, appointment);
        if (appointment1 != null) {
            appointmentService.deleteAppointment(appointment1);
            return ResponseEntity.status(HttpStatus.OK).body("Appointment deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not delete the appointment, " +
                "most possibly it is not found.");
    }
    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @DeleteMapping("/deleteAppointment/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @Transactional
    public ResponseEntity<?> deleteAppointmentById(@PathVariable Long appointmentId) {
        User doctorAsUser = userService
                .findByFirebaseId(SecurityContextHolder.getContext()
                        .getAuthentication().getName());
        if (doctorAsUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized, either user is not a " +
                    "doctor or the user cannot be found.");
        }

        Doctor doctor = doctorService.getDoctorById(doctorAsUser.getPatMedAdminId());

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null || !appointment.getDoctor().getId().equals(doctor.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Appointment not found or does not belong to doctor.");
        }

        appointmentService.deleteAppointment(appointment);
        return ResponseEntity.status(HttpStatus.OK).body("Appointment deleted successfully.");
    }

}
