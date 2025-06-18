package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.Medication;
import com.hospital.hospital_backend.entities.MedicationCategory;
import com.hospital.hospital_backend.repositories.MedicationRepository;
import com.hospital.hospital_backend.payloads.medication.AddMedicationPayload;
import com.hospital.hospital_backend.payloads.medication.MedicationPayload;
import com.hospital.hospital_backend.payloads.medication.UpdateStockMedicationPayload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MedicationService {
    private final MedicationRepository medicationRepository;

    public record MedicationDTO(Long id, String name, int stock, String category) {}

    public List<MedicationDTO> getMedicationsContainingName(String subName) {
        return medicationRepository.findAllByNameContainsIgnoreCase(subName)
                .stream().map(a -> new MedicationDTO(
                        a.getId(),
                        a.getName(),
                        a.getStock(),
                        a.getCategory().getName())).toList();
    }

    public Boolean requestMedications(List<MedicationPayload> medications) {
        for (MedicationPayload medication : medications) {
            Optional<Medication> med = medicationRepository.findByIdOrName(medication.getMedicationId(), medication.getMedicationName());
            if (med.isEmpty()) {
                return false;
            }
            Medication medication1 = med.get();
            if (medication1.getStock() < medication.getRequiredQuantity()) {
                return false;
            }
        }

        for (MedicationPayload medication : medications) {
            Optional<Medication> med = medicationRepository.findByIdOrName(medication.getMedicationId(), medication.getMedicationName());
            if (med.isEmpty()) {
                return false;
            }

            Medication medication1 = med.get();
            medication1.setStock(medication1.getStock() - medication.getRequiredQuantity());
            medicationRepository.save(medication1);
        }

        return true;
    }

    public Boolean updateStock(UpdateStockMedicationPayload medication) {
        Optional<Medication> med = medicationRepository.findByIdOrName(medication.getMedicationId(), medication.getMedicationName());
        if (med.isEmpty()) {
            return false;
        }
        Medication medication1 = med.get();
        medication1.setStock(medication1.getStock() + medication.getStock());
        medicationRepository.save(medication1);
        return true;
    }

    public Boolean saveMedication(AddMedicationPayload medication, MedicationCategory medicationCategory) {
        if (medicationRepository.findByName(medication.getName()).isPresent()) {
            return false;
        }
        Medication medication1 = new Medication(medication.getName(), medication.getStartStock(), medicationCategory);
        medicationRepository.save(medication1);
        return true;
    }

    public Medication getMedicationByName(String name) {
        return medicationRepository.findByName(name).orElse(null);
    }

    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id).orElse(null);
    }

    public Boolean deleteMedication(Long id) {
        Medication medication = medicationRepository.findById(id).orElse(null);
        if (medication == null) {
            return false;
        }
        medicationRepository.delete(medication);
        return true;
    }
}
