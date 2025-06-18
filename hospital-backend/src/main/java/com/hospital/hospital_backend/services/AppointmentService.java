package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.Appointment;
import com.hospital.hospital_backend.entities.Doctor;
import com.hospital.hospital_backend.entities.Patient;
import com.hospital.hospital_backend.repositories.AppointmentRepository;
import com.hospital.hospital_backend.payloads.appointment.AppointmentPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@Service
public class AppointmentService {
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentRepository appointmentRepository;
    public Long addNewAppointment(Doctor doctor,
                                  Patient patient,
                                  AppointmentPayload appointmentPayload) {
        Appointment appointment =
                new Appointment(appointmentPayload.getDate(), patient, doctor, appointmentPayload.getDuration());
        LocalDateTime start = appointment.getDate();
        LocalDateTime end = appointment.getDate().plusMinutes(appointment.getDuration());
        if (overlappedTime(start, end, doctor.getAppointments()) ||
                overlappedTime(start, end, patient.getAppointments())) {
            return -1L;
        }
        appointment = appointmentRepository.save(appointment);
        doctor.getAppointments().add(appointment);
        patient.getAppointments().add(appointment);
        doctorService.saveDoctor(doctor);
        patientService.savePatient(patient);
        return appointment.getId();
    }

    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    private boolean overlappedTime(LocalDateTime start, LocalDateTime end, List<Appointment> appointments) {
        for (Appointment app: appointments) {
            LocalDateTime startApp = app.getDate();
            LocalDateTime endApp = app.getDate().plusMinutes(app.getDuration());
            if (startApp.isAfter(start) && startApp.isBefore(end) ||
                    (startApp.isEqual(start))) {
                return true;
            }
            if (endApp.isAfter(start) && endApp.isBefore(end) ||
                    (endApp.isEqual(end))) {
                return true;
            }
            if (startApp.isBefore(start) && endApp.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }
    public List<Map<String, String>> getAppointmentsForPatient(Patient patient) {
        List<Map<String, String>> appointments = new ArrayList<>();
        List<Appointment> appointmentList = appointmentRepository.findByPatient(patient);
        for (Appointment appointment : appointmentList) {
            Map<String, String> appointmentMap = new HashMap<>();
            appointmentMap.put("date", appointment.getDate().toString());
            appointmentMap.put("doctor_name", doctorService.getDoctorFullName(appointment.getDoctor()));
            appointmentMap.put("duration(min)", appointment.getDuration().toString());
            appointmentMap.put("patient_cnp", appointment.getPatient().getCnp());
            appointmentMap.put("app_id", appointment.getId().toString());
            appointments.add(appointmentMap);
        }
        return appointments;
    }
}
