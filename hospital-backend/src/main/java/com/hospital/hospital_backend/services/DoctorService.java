package com.hospital.hospital_backend.services;

import com.hospital.hospital_backend.entities.*;
import com.hospital.hospital_backend.repositories.DoctorRepository;
import com.hospital.hospital_backend.repositories.UserRepository;
import com.hospital.hospital_backend.payloads.appointment.AppointmentDatePayload;
import com.hospital.hospital_backend.payloads.management.DoctorPayload;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PatientService patientService;
    private final UserRepository userRepository;
    public int saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
        return 201;
    }

    @Transactional
    public void deleteDoctor(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }

    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId).orElse(null);
    }

    public List<Map<String, String>> getAppointments(Doctor doctor) {
        List<Map<String, String>> appointments = new ArrayList<>();
        List<Appointment> appointmentList = doctor.getAppointments();
        for (Appointment appointment : appointmentList) {
            Map<String, String> appointmentMap = new HashMap<>();
            appointmentMap.put("date", appointment.getDate().toString());
            appointmentMap.put("duration(min)", appointment.getDuration().toString());
            appointmentMap.put("patient_cnp", appointment.getPatient().getCnp());
            appointmentMap.put("app_id", appointment.getId().toString());
            appointments.add(appointmentMap);
        }
        return appointments;
    }
    public List<Map<String, String>> getAppointmentsPerDay(Doctor doctor, AppointmentDatePayload appointmentDatePayload) {
        List<Map<String, String>> appointments = new ArrayList<>();
        List<Appointment> appointmentList = doctor.getAppointments();
        for (Appointment appointment : appointmentList) {
            if (appointment.getDate().getYear() == appointmentDatePayload.getYear() &&
            appointment.getDate().getMonthValue() == appointmentDatePayload.getMonth() &&
            appointment.getDate().getDayOfMonth() == appointmentDatePayload.getDay()) {
                Map<String, String> appointmentMap = new HashMap<>();
                appointmentMap.put("date", appointment.getDate().toString());
                appointmentMap.put("duration(min)", appointment.getDuration().toString());
                appointmentMap.put("patient_cnp", appointment.getPatient().getCnp());
                appointmentMap.put("app_id", appointment.getId().toString());
                appointments.add(appointmentMap);
            }
        }
        return appointments;
    }

    public Appointment  deleteAppointment(Doctor doctor, AppointmentDatePayload appointmentDatePayload) {
        Appointment appointment = null;
        if (appointmentDatePayload.getId() != 0) {
            appointment = doctor.getAppointments().stream().filter(a -> a.getId() == appointmentDatePayload.getId()).findFirst().orElse(null);
        }
        if (appointment == null) {
            appointment = doctor.getAppointments().stream().filter(a -> (a.getDate()
                    .getMonthValue() == appointmentDatePayload.getMonth() &&
                    a.getDate().getDayOfMonth() == appointmentDatePayload.getDay() &&
                    a.getDate().getYear() == appointmentDatePayload.getYear() &&
                    a.getDate().getHour() == appointmentDatePayload.getHour() &&
                    a.getDate().getMinute() == appointmentDatePayload.getMinute())).findFirst().orElse(null);
        }
        if (appointment == null) {
            return null;
        }
        Patient patient = appointment.getPatient();
        patient.getAppointments().remove(appointment);
        doctor.getAppointments().remove(appointment);
        doctorRepository.save(doctor);
        patientService.savePatient(patient);
        return appointment;
    }
    public String getDoctorFullName(Doctor doctor) {
        Optional<User> user = userRepository.findByPatMedAdminIdAndRole(doctor.getId(), ERole.ROLE_DOCTOR);
        if (!user.isPresent()) {
            return "noNAME";
        }
        return user.get().getFirstName() + " " + user.get().getLastName();
    }
    public User getDoctorAsUser(Doctor doctor) {
        Optional<User> user = userRepository.findByPatMedAdminIdAndRole(doctor.getId(), ERole.ROLE_DOCTOR);
        if (!user.isPresent()) {
            return null;
        }
        return user.get();
    }
    public List<DoctorPayload> getAllDoctorPayloads() {
        return doctorRepository.findAll().stream()
                .map(doctor -> {
                    User user = getDoctorAsUser(doctor);
                    if (user == null) return null;
                    return new DoctorPayload(
                            doctor.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.getUsername()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public boolean deleteDoctorById(Long doctorId) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            User user = getDoctorAsUser(doctor);

            // 1. Ștergem întâi doctorul (ca să nu încalce constrângerile FK)
            doctorRepository.delete(doctor);

            // 2. Apoi ștergem și user-ul asociat
            userRepository.delete(user);

            return true;
        }
        return false;
    }
}
