package com.hospital.hospital_backend.payloads.management;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentityPayload {
    private String username;
    @Email
    private String email;
    private String cnp;
}
