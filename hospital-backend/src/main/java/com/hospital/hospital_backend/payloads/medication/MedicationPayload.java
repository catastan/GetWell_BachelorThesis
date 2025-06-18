package com.hospital.hospital_backend.payloads.medication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicationPayload {
    private Long medicationId;
    private String medicationName;
    private int requiredQuantity;
}
