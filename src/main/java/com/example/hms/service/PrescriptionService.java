package com.example.hms.service;

import com.example.hms.dto.request.PrescriptionRequestDTO;
import com.example.hms.dto.response.PrescriptionResponseDTO;

import java.util.List;

public interface PrescriptionService {
    List<PrescriptionResponseDTO> getAllPrescriptions();
    PrescriptionResponseDTO getPrescriptionById(Long id);
    PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto);
    PrescriptionResponseDTO updatePrescription(Long id, PrescriptionRequestDTO dto);
    void deletePrescription(Long id);
    List<PrescriptionResponseDTO> getPrescriptionsByPatientId(Long patientId);
    List<PrescriptionResponseDTO> getPrescriptionsByDoctorId(Long doctorId);

}
