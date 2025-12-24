package com.example.hms.service;

import com.example.hms.dto.request.MedicalRecordRequestDTO;
import com.example.hms.dto.response.MedicalRecordResponseDTO;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordResponseDTO> getAllRecords();
    List<MedicalRecordResponseDTO> getRecordsByPatientId(Long patientId);

    // ✅ Doktora göre kayıtları getirme
    List<MedicalRecordResponseDTO> getRecordsByDoctorId(Long doctorId);
    MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO dto);
    MedicalRecordResponseDTO updateRecord(Long id, MedicalRecordRequestDTO dto);
    void deleteRecord(Long id);
    MedicalRecordResponseDTO getRecordById(Long id);
}
