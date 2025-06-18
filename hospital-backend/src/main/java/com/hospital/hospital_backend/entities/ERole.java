package com.hospital.hospital_backend.entities;

public enum ERole {
    ROLE_ADMIN,
    ROLE_DOCTOR,
    ROLE_PATIENT;

    // Method to get enum from string
    public static ERole fromString(String status) {
        for (ERole s : ERole.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant for status: " + status);
    }
}
