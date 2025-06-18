package com.hospital.hospital_backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.hospital.hospital_backend.entities.ERole;
import com.hospital.hospital_backend.entities.User;
import com.hospital.hospital_backend.services.UserService;
import com.hospital.hospital_backend.payloads.management.EncryptPasswordUtilityClass;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class StartupRunner implements CommandLineRunner {
    private final UserService userService;
    private final FirebaseAuth firebaseAuth;
    private final EncryptPasswordUtilityClass encryptPasswordUtilityClass;
    @Override
    public void run(String... args) throws Exception {
        List<User> users = userService.findAll();
        if (users.isEmpty() || users.stream().noneMatch(a -> a.getRole().equals(ERole.ROLE_ADMIN))) {
            String email = "admin@hospital.com";
            String password = "hospital";
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(encryptPasswordUtilityClass.encryptPassword(password));
            UserRecord userRecord = firebaseAuth.createUser(request);
            String userId = userRecord.getUid();
            ERole role = ERole.ROLE_ADMIN;
            userService.saveUser(new User(role, email, "admin", (long) -1,
                    userId, "admin", "admin", "0777777777",
                    encryptPasswordUtilityClass.encryptPassword(password)));
        }
    }
}
