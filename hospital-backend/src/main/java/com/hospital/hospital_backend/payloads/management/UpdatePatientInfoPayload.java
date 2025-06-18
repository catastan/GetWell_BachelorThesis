package com.hospital.hospital_backend.payloads.management;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePatientInfoPayload {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private String email;
}
