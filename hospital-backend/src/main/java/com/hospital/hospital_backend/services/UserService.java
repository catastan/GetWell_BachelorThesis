package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.Doctor;
import com.hospital.hospital_backend.entities.ERole;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.repositories.UserRepository;
import com.hospital.hospital_backend.payloads.management.EncryptPasswordUtilityClass;
import com.hospital.hospital_backend.payloads.management.UpdatePatientInfoPayload;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final EncryptPasswordUtilityClass encryptPasswordUtilityClass;

    public boolean alreadyExistsInDatabase(String email, String username) {
        return userRepository.existsByEmail(email) || userRepository.existsByUsername(username);
    }

    @Transactional
    public int saveUser(User user) {
        userRepository.save(user);
        return 201;
    }

    @Transactional
    public int saveUserAsPatient(User user, Patient patient) {

        if (alreadyExistsInDatabase(user.getEmail(), user.getUsername())) {
            return 409;
        }

        int ansPatient = patientService.savePatient(patient);
        if (ansPatient != 201) {
            return 400;
        }
        user.setPatMedAdminId(patient.getId());
        int ans = saveUser(user);
        if (ans != 201) {
            return 400;
        }
        return 201;
    }

    @Transactional
    public int saveUserAsDoctor(User user, Doctor doctor) {

        if (alreadyExistsInDatabase(user.getEmail(), user.getUsername())) {
            return 409;
        }

        int ansDoctor = doctorService.saveDoctor(doctor);
        if (ansDoctor != 201) {
            return 400;
        }
        user.setPatMedAdminId(doctor.getId());
        int ans = saveUser(user);
        if (ans != 201) {
            return 400;
        }

        return 201;
    }

    public User findByFirebaseId(String firebaseId) {
        return userRepository.findByFirebaseId(firebaseId).orElse(null);
    }

    @Transactional
    public void deleteUser(User user) {
        if (user.getRole() == ERole.ROLE_PATIENT) {
            patientService.deletePatientById(user.getPatMedAdminId());
        } else if (user.getRole() == ERole.ROLE_DOCTOR) {
            doctorService.deleteDoctor(user.getPatMedAdminId());
        }
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPass(newPassword);
        userRepository.save(user);
    }

    public boolean passwordMatch(User user, String password) {
        return user.getPass().equals(password);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void modifyUser(User user, UpdatePatientInfoPayload updatePatientInfoPayload) {
        Map<Supplier<String>, Consumer<String>> setters = Map.of(
                updatePatientInfoPayload::getFirstName, user::setFirstName,
                updatePatientInfoPayload::getLastName, user::setLastName,
                updatePatientInfoPayload::getPhoneNumber, user::setPhoneNumber,
                updatePatientInfoPayload::getUsername, user::setUsername,
                updatePatientInfoPayload::getEmail, user::setEmail
        );

        setters.forEach((getter, setter) -> {
            String value = getter.get();
            if (value != null && !value.isEmpty()) {
                setter.accept(value);
            }
        });
        userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


}
