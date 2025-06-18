package com.hospital.hospital_backend.payloads.management;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalDiagnosisPayload {
    private String username;
    @Email
    private String email;
    private String cnp;
    @NotBlank
    private String diagnosis;
    private LocalDate diagnosisDate;
}
