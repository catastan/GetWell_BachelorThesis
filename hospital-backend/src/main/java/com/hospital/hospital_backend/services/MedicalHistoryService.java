package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.MedicalHistory;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.repositories.MedicalHistoryRepository;
import com.hospital.hospital_backend.payloads.management.AddMedicalInfoPayload;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class MedicalHistoryService {
    private final MedicalHistoryRepository medicalHistoryRepository;

    @Transactional
    public void saveMedicalHistory(Patient patient) {
        if (patient.getMedicalHistory() == null) {
            String folderName = patient.getCnp() + "MedicalHistory";
            patient.setMedicalHistory(new MedicalHistory(folderName, patient));
            medicalHistoryRepository.save(patient.getMedicalHistory());
        }
    }

    @Transactional
    public ResponseEntity<String> addFilesToMedicalHistory(Patient patient, AddMedicalInfoPayload addMedicalInfoPayload) {
        String parentFolderName = System.getProperty("user.home") + "/Facultate/ANUL4/licenta_catalin/MedicalFolder";
        if (!createParentFolder(parentFolderName)) {
            return ResponseEntity.badRequest().body("Couldn't create folder");
        }
        String patientFolderName = parentFolderName + "/" + patient.getMedicalHistory().getFolderName();
        if (!createParentFolder(patientFolderName)) {
            return ResponseEntity.badRequest().body("Couldn't create folder");
        }
        String prefixFileName = parentFolderName + "/" + patient.getMedicalHistory().getFolderName() + "/";
        for (MultipartFile file : addMedicalInfoPayload.getFiles()) {
            try {
                file.transferTo(new File(prefixFileName + file.getOriginalFilename()));
            } catch (IOException e) {
                throw new RuntimeException("Couldn't save file: " + file.getOriginalFilename(), e);
            }
        }
        return ResponseEntity.ok().body("Successfully added files");
    }

    private boolean createParentFolder(String folderName) {
        File parentFolder = new File(folderName);
        return parentFolder.exists() ? true : parentFolder.mkdirs();
    }
}
