package com.hospital.hospital_backend.services;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Service
public class AuthService {
    private final FirebaseAuth firebaseAuth;

    public String loginWithEmailAndPassword(String email, String password) {

        String firebaseApiKey = System.getenv("FIREBASE_API_KEY");
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

        Map<String, String> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", "true");

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.postForObject(url, payload, Map.class);
            if (response == null) {
                throw new RuntimeException("Could not sign in, not a response given.");
            }
            return (String) response.get("idToken");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public String verifyIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            return decodedToken.getUid();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }
}
