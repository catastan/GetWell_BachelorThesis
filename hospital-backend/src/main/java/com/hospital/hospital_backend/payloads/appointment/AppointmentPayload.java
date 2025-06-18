package com.hospital.hospital_backend.payloads.appointment;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentPayload {
    private String username;
    @Email
    private String email;
    private String cnp;
    @NotNull
    private LocalDateTime date;
    @NotNull
    private Integer duration;
}
