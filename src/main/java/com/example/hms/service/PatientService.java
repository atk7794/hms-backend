// PatientService.java (interface)
package com.example.hms.service;

import com.example.hms.dto.request.PatientRequestDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.model.Patient;

import java.util.List;

public interface PatientService {
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO createPatient(PatientRequestDTO patientDTO);
    PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientDTO);

    boolean existsByUserId(Long userId);

    void updatePatientByUserId(PatientRequestDTO dto);

    // 🔹 EKLE
    PatientResponseDTO getPatientByUserId(Long userId);

    void deletePatient(Long id);

    PatientResponseDTO convertToDTO(Patient patient);

    Patient getPatientEntityById(Long id);


}
