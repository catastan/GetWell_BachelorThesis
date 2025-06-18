package com.hospital.hospital_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ERole role;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(name = "first name", nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "pat_med_admin_id", nullable = false)
    private Long patMedAdminId;

    @Column(name = "firebase_id")
    private String firebaseId;

    @Column(name = "pass")
    private String pass;

    public User(ERole role, String email, String username, Long patMedAdminId, String firebaseId, String firstName, String lastName, String phoneNumber, String pass) {
        this.role = role;
        this.email = email;
        this.username = username;
        this.patMedAdminId = patMedAdminId;
        this.firebaseId = firebaseId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.pass = pass;
    }
}
