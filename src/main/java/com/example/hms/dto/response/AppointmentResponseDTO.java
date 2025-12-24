package com.example.hms.dto.response;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {
    private Long id;
    private PatientResponseDTO patient;
    private DoctorResponseDTO doctor;
    private LocalDateTime appointmentDate;
    private String status;

    public AppointmentResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PatientResponseDTO getPatient() { return patient; }
    public void setPatient(PatientResponseDTO patient) { this.patient = patient; }

    public DoctorResponseDTO getDoctor() { return doctor; }
    public void setDoctor(DoctorResponseDTO doctor) { this.doctor = doctor; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
