package com.example.hms.service;

import com.example.hms.dto.request.DoctorRequestDTO;
import com.example.hms.dto.response.DoctorResponseDTO;
import com.example.hms.model.Doctor;

import java.util.List;

public interface DoctorService {

    // Tüm doktorları getir
    List<DoctorResponseDTO> getAllDoctors();

    // ID ile tek bir doktoru getir
    DoctorResponseDTO getDoctorById(Long id);


    DoctorResponseDTO getDoctorByUserId(Long userId);

    // Yeni doktor ekle
    DoctorResponseDTO createDoctor(DoctorRequestDTO doctorDTO);

    // ID ile doktor güncelle
    DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorDTO);

    // ID ile doktor sil
    void deleteDoctor(Long id);


    List<String> getAllSpecialities();

    List<DoctorResponseDTO> getDoctorsBySpeciality(String speciality);

    DoctorResponseDTO convertToDTO(Doctor doctor);

    Doctor getDoctorEntityById(Long id);


}
