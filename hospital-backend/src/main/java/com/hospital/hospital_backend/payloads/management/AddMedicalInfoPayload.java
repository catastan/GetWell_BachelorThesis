package com.hospital.hospital_backend.payloads.management;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMedicalInfoPayload {
    private String username;
    @Email
    private String email;
    private String cnp;
    private List<MultipartFile> files;
}
