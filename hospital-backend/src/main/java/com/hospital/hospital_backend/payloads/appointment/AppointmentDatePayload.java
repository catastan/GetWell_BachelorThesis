package com.hospital.hospital_backend.payloads.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDatePayload {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private long id;
}
