package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.ERole;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.payloads.management.UserIdentityPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UserPatientService {
    private final UserService userService;
    private final PatientService patientService;

    public Patient findPatientByIdentityPayload(UserIdentityPayload userIdentityPayload) {
        Patient patient = patientService.findPatientByCnp(userIdentityPayload.getCnp());
        if (patient != null) {
            return patient;
        }
        User user = userService.findUserByUsername(userIdentityPayload.getUsername());

        if (user == null) {
            user = userService.findUserByEmail(userIdentityPayload.getEmail());
        }
        if (user == null) {
            return null;
        }
        if (!user.getRole().equals(ERole.ROLE_PATIENT)) {
            return null;
        }
        return patientService.findById(user.getPatMedAdminId());
    }
}
