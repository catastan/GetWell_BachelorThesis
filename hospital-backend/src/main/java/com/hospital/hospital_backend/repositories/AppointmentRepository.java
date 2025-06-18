package com.hospital.hospital_backend.repositories;

import com.hospital.hospital_backend.entities.Appointment;
import com.hospital.hospital_backend.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(Patient patient);
}
