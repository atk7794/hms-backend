package com.example.hms.service;

import com.example.hms.dto.request.AppointmentRequestDTO;
import com.example.hms.dto.response.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {

    // Tüm randevuları getir
    List<AppointmentResponseDTO> getAllAppointments();

    // ID ile tek bir randevuyu getir
    AppointmentResponseDTO getAppointmentById(Long id);

    // Yeni randevu ekle
    AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequestDTO);

    // Randevu güncelle
    AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO appointmentRequestDTO);

    // Randevu sil
    void deleteAppointment(Long id);

    // hasta/doctor bazlı sorgulamalar için mevcut metodların altına ekle
    List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId);

    List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId);

}
