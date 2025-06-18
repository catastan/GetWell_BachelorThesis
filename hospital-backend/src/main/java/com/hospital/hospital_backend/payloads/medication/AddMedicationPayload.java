package com.hospital.hospital_backend.payloads.medication;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMedicationPayload {
    private String name;
    private String category;
    private int startStock;
}
