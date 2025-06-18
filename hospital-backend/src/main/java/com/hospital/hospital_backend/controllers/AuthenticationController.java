package com.hospital.hospital_backend.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.hospital.hospital_backend.entities.Doctor;
import com.hospital.hospital_backend.entities.ERole;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.repositories.UserRepository;
import com.hospital.hospital_backend.payloads.request.ChangePasswordRequest;
import com.hospital.hospital_backend.payloads.request.SignInRequest;
import com.hospital.hospital_backend.payloads.request.SignUpRequest;
import com.hospital.hospital_backend.services.AuthService;
import com.hospital.hospital_backend.services.DoctorService;
import com.hospital.hospital_backend.services.UserService;
import com.hospital.hospital_backend.payloads.management.DoctorPayload;
import com.hospital.hospital_backend.payloads.management.EncryptPasswordUtilityClass;
import com.hospital.hospital_backend.payloads.management.UpdatePatientInfoPayload;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/v1")
@AllArgsConstructor
public class AuthenticationController {

    private final FirebaseAuth firebaseAuth;
    private final UserService userService;
    private final EncryptPasswordUtilityClass encryptPasswordUtilityClass;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final DoctorService doctorService;

    @PostMapping("/doctor/signup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<String> doctorSignup(@RequestBody SignUpRequest signUpRequest) {
        try {
            if (userService.alreadyExistsInDatabase(signUpRequest.getEmail(), signUpRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists in database");
            }
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(signUpRequest.getEmail())
                    .setPassword(encryptPasswordUtilityClass.encryptPassword(signUpRequest.getPassword()));

            UserRecord userRecord = firebaseAuth.createUser(request);

            String userId = userRecord.getUid();
            ERole role = ERole.ROLE_DOCTOR;
            int ans = userService
                    .saveUserAsDoctor(new User(role, signUpRequest.getEmail(), signUpRequest.getUsername(), (long) -1, userId, signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getPhoneNumber(), encryptPasswordUtilityClass.encryptPassword(signUpRequest.getPassword())),
                            new Doctor());
            if (ans == 409) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists in database");
            } else if (ans == 400) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong when saving the user");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Înregistrare reușită");

        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @GetMapping("/doctors")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllDoctors() {
        try {
            List<DoctorPayload> doctors = doctorService.getAllDoctorPayloads();
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch doctors: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @DeleteMapping("/doctors/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        try {
            boolean deleted = doctorService.deleteDoctorById(doctorId);
            if (deleted) {
                return ResponseEntity.ok("Doctor deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting doctor: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" })
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest) {
        try {
            if (userService.alreadyExistsInDatabase(signUpRequest.getEmail(), signUpRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists in database");
            }
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(signUpRequest.getEmail())
                    .setPassword(encryptPasswordUtilityClass.encryptPassword(signUpRequest.getPassword()));

            UserRecord userRecord = firebaseAuth.createUser(request);

            String userId = userRecord.getUid();
            ERole role = ERole.ROLE_PATIENT;
            int ans = userService
                    .saveUserAsPatient(new User(role, signUpRequest.getEmail(), signUpRequest.getUsername(), (long) -1, userId, signUpRequest.getFirstName(), signUpRequest.getLastName(), signUpRequest.getPhoneNumber(), encryptPasswordUtilityClass.encryptPassword(signUpRequest.getPassword())),
                            new Patient(signUpRequest.getCnp()));
            if (ans == 409) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists in database");
            } else if (ans == 400) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong when saving the user");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Înregistrare reușită");

        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating user: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest signInRequest) throws FirebaseAuthException {
        User user = userRepository.findByEmail(signInRequest.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
        if (!user.getPass().equals(encryptPasswordUtilityClass.encryptPassword(signInRequest.getPassword()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Incorrect password"));
        }
        UserRecord ur = firebaseAuth.getUserByEmail(signInRequest.getEmail());
        if (ur == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }
        String idToken = authService.loginWithEmailAndPassword(signInRequest.getEmail(), encryptPasswordUtilityClass.encryptPassword(signInRequest.getPassword()));

        Map<ERole, String> rolesMap = Map.of(ERole.ROLE_PATIENT, "patient", ERole.ROLE_DOCTOR, "doctor", ERole.ROLE_ADMIN, "admin");
        return ResponseEntity.ok()
                .body(Map.of("message", "Authenticated!", "role", rolesMap.get(user.getRole()), "accessToken", idToken));
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        try {
            String uid = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByFirebaseId(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            firebaseAuth.deleteUser(uid);
            userService.deleteUser(user);
            return ResponseEntity.ok().body("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PutMapping("/changePass")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            String uid = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByFirebaseId(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            changePasswordRequest.setOldPassword(encryptPasswordUtilityClass
                    .encryptPassword(changePasswordRequest.getOldPassword()));
            changePasswordRequest.setNewPassword(encryptPasswordUtilityClass
                    .encryptPassword(changePasswordRequest.getNewPassword()));
            if (userService.passwordMatch(user, changePasswordRequest.getOldPassword())) {
                try {
                    UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid);
                    request.setPassword(changePasswordRequest.getNewPassword());
                    firebaseAuth.updateUser(request);
                    userService.changePassword(user, changePasswordRequest.getNewPassword());
                    return ResponseEntity.ok().body("Password changed successfully!");
                } catch (FirebaseAuthException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Cannot change password in firebase");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot change password");
    }

    @CrossOrigin(origins = { "http://localhost:3000", "http://hospital-frontend:3000" }, allowCredentials = "true")
    @PutMapping("/changeData")
    public ResponseEntity<?> changeData(@RequestBody UpdatePatientInfoPayload updatePatientInfoPayload) {
        try {
            String uid = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.findByFirebaseId(uid);
            if (user != null) {
                if (!updatePatientInfoPayload.getEmail().isEmpty()) {
                    if (!user.getEmail().equals(updatePatientInfoPayload.getEmail())) {
                        if (userService.alreadyExistsInDatabase(updatePatientInfoPayload.getEmail(), updatePatientInfoPayload.getUsername())) {
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists in database");
                        }
                    }
                    firebaseAuth.updateUser(new UserRecord.UpdateRequest(uid).setEmail(updatePatientInfoPayload.getEmail()));
                }
                userService.modifyUser(user, updatePatientInfoPayload);
                return ResponseEntity.ok().body("User updated successfully!");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found in database");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }

    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = authorizationHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String uid = decodedToken.getUid();
            User user = userService.findByFirebaseId(uid);

            return ResponseEntity.ok(Map.of("role", user.getRole()));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalid: " + e.getMessage());
        }
    }

}
