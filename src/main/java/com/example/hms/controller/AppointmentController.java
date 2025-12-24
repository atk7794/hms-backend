package com.example.hms.controller;

import com.example.hms.dto.request.AppointmentRequestDTO;
import com.example.hms.dto.response.AppointmentResponseDTO;
import com.example.hms.dto.response.DoctorResponseDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.service.AppointmentService;
import com.example.hms.service.UserActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:4200")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserActionLogService userActionLogService;

    // Tüm randevuları listele
    @GetMapping
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // ID ile tek bir randevuyu getir
    @GetMapping("/{id}")
    public AppointmentResponseDTO getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    // Yeni randevu oluştur
    @PostMapping
    public AppointmentResponseDTO createAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        AppointmentResponseDTO response = appointmentService.createAppointment(appointmentRequestDTO);

        System.out.println("📩 Gelen Patient ID: " + appointmentRequestDTO.getPatientId());
        System.out.println("📩 Gelen Doctor ID: " + appointmentRequestDTO.getDoctorId());

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponseDTO patient = response.getPatient();
        DoctorResponseDTO doctor = response.getDoctor();
        userActionLogService.logAction(username, "CREATE_APPOINTMENT",
                "Created appointment (ID: " + response.getId() + ") for patient " +
                        patient.getFirstName() + " " + patient.getLastName() +
                        " with doctor " + doctor.getFirstName() + " " + doctor.getLastName() +
                        " on " + response.getAppointmentDate());

        return response;
    }

    // ID ile randevuyu güncelle
    @PutMapping("/{id}")
    public AppointmentResponseDTO updateAppointment(@PathVariable Long id,
                                                    @Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        AppointmentResponseDTO response = appointmentService.updateAppointment(id, appointmentRequestDTO);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponseDTO patient = response.getPatient();
        DoctorResponseDTO doctor = response.getDoctor();
        userActionLogService.logAction(username, "UPDATE_APPOINTMENT",
                "Updated appointment (ID: " + id + ") for patient " +
                        patient.getFirstName() + " " + patient.getLastName() +
                        " with doctor " + doctor.getFirstName() + " " + doctor.getLastName() +
                        " on " + response.getAppointmentDate());

        return response;
    }

    // ID ile randevuyu sil
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        AppointmentResponseDTO response = appointmentService.getAppointmentById(id);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PatientResponseDTO patient = response.getPatient();
        DoctorResponseDTO doctor = response.getDoctor();
        userActionLogService.logAction(username, "DELETE_APPOINTMENT",
                "Deleted appointment (ID: " + id + ") for patient " +
                        patient.getFirstName() + " " + patient.getLastName() +
                        " with doctor " + doctor.getFirstName() + " " + doctor.getLastName() +
                        " on " + response.getAppointmentDate());

        appointmentService.deleteAppointment(id);
    }


    // ... mevcut CRUD metotlarının altına ekle

    // Hastaya ait tüm randevuları getir id ye göre
    @GetMapping("/patient/{patientId}")
    public List<AppointmentResponseDTO> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatientId(patientId);
    }

    // Doktora ait tüm randevuları getir id ye göre
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResponseDTO> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }

}
