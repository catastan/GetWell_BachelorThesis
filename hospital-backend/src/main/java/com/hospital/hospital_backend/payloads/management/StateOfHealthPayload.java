package com.hospital.hospital_backend.payloads.management;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StateOfHealthPayload {
    @NotBlank
    private String stateOfHealth;
    private String username;
    @Email
    private String email;
    private String cnp;
}
