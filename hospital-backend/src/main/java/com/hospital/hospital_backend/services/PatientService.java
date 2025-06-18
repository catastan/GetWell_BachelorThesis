package com.hospital.hospital_backend.services;

import com.google.api.client.util.IOUtils;
import com.hospital.hospital_backend.entities.PastDiagnosis;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.repositories.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalHistoryService medicalHistoryService;

    @Transactional
    public int savePatient(Patient patient) {
        Optional<Patient> patientOptional = patientRepository.findByCnp(patient.getCnp());
        if (patientOptional.isPresent()) {
            return 400;
        }
        patientRepository.save(patient);
        medicalHistoryService.saveMedicalHistory(patient);
        patientRepository.save(patient);
        return 201;
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletePatientById(Long id) {
        patientRepository.deleteById(id);
    }

    @Transactional
    public void updatePatient(Patient patient, String stateOfHealth) {
        patient.setActualStateOfHealth(stateOfHealth);
        patientRepository.save(patient);
    }

    public Patient findPatientByCnp(String cnp) {
        return patientRepository.findByCnp(cnp).orElse(null);
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean updateStateOfHealth(Patient patient, String stateOfHealth) {
        patient.setActualStateOfHealth(stateOfHealth);
        Patient aux = patientRepository.save(patient);
        return aux != null;
    }

    public List<Map<String, String>> getMedicalDiagnoses(Patient patient) {
        List<Map<String, String>> diagnoses = new ArrayList<>();
        for (PastDiagnosis diagnosis : patient.getPastDiagnoses()) {
            diagnoses.add(Map.of("diagnosis", diagnosis.getDiagnosis(),
                    "date", diagnosis.getDate().toString()));
        }
        return diagnoses;
    }

    public ResponseEntity<byte[]> getMedicalHistory(Patient patient) throws IOException {
        if (patient.getMedicalHistory() == null) {
            return ResponseEntity.status(404).build();
        }
        String filename = patient.getCnp() + "-medical-history.zip";
        File directoryPatient = new File(System.getProperty("user.home") + "/Facultate/ANUL4/licenta_catalin/MedicalFolder/" + patient.getMedicalHistory().getFolderName());
        if (!directoryPatient.exists()) {
            boolean created = directoryPatient.mkdirs();
            if (!created) {
                return ResponseEntity.status(500).build();
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            File directory = new File(System.getProperty("user.home") + "/Facultate/ANUL4/licenta_catalin/MedicalFolder/" + patient.getMedicalHistory().getFolderName());
            File[] fileArray = directory.listFiles();

            if (fileArray == null || fileArray.length == 0) {
                zipOutputStream.putNextEntry(new ZipEntry(directory.getName() + "/"));
                zipOutputStream.closeEntry();
            } else {
                for (File file : fileArray) {
                    if (file.isFile()) {
                        zipOutputStream.putNextEntry(new ZipEntry(directory.getName() + "/" + file.getName()));
                        try (FileInputStream fileInputStream = new FileInputStream(file)) {
                            IOUtils.copy(fileInputStream, zipOutputStream);
                        }
                        zipOutputStream.closeEntry();
                    }
                }
            }
            zipOutputStream.flush();
        }

        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(zipBytes);
    }
}
