package com.hospital.hospital_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String credentials_file = System.getenv("FIREBASE_CREDENTIALS_FILE");
            FileInputStream serviceAccount =
                    new FileInputStream(credentials_file);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            return app;
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth initializeFirebaseAuth(FirebaseApp app) {
        return FirebaseAuth.getInstance(app);
    }
}
