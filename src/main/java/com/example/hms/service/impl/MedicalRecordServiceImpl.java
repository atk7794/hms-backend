package com.example.hms.service.impl;

import com.example.hms.dto.request.MedicalRecordRequestDTO;
import com.example.hms.dto.response.MedicalRecordResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.MedicalRecord;
import com.example.hms.model.Patient;
import com.example.hms.repository.MedicalRecordRepository;
import com.example.hms.service.MedicalRecordService;
import com.example.hms.service.PatientService;
import com.example.hms.service.DoctorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    PatientService patientService,
                                    DoctorService doctorService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    // ✅ TÜM KAYITLARI LİSTELE
    @Override
    public List<MedicalRecordResponseDTO> getAllRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ HASTAYA GÖRE KAYIT LİSTELE
    @Override
    public List<MedicalRecordResponseDTO> getRecordsByPatientId(Long patientId) {
        Patient patient = patientService.getPatientEntityById(patientId);
        return medicalRecordRepository.findByPatient(patient)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // doktara göre kayıt listele
    @Override
    public List<MedicalRecordResponseDTO> getRecordsByDoctorId(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    // ✅ YENİ KAYIT OLUŞTUR
    @Override
    public MedicalRecordResponseDTO createRecord(MedicalRecordRequestDTO dto) {
        MedicalRecord record = toEntity(dto);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return toDTO(medicalRecordRepository.save(record));
    }

    // ✅ GÜNCELLE
    @Override
    public MedicalRecordResponseDTO updateRecord(Long id, MedicalRecordRequestDTO dto) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord not found with id: " + id));

        existing.setPatient(patientService.getPatientEntityById(dto.getPatientId()));
        existing.setDoctor(doctorService.getDoctorEntityById(dto.getDoctorId()));
        existing.setDiagnosis(dto.getDiagnosis());
        existing.setPrescription(dto.getPrescription());
        existing.setNotes(dto.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());

        return toDTO(medicalRecordRepository.save(existing));
    }

    // ✅ SİL
    @Override
    public void deleteRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("MedicalRecord not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }


    // bunu doktor için kullanıcaktık sonra kullanmadık sanırım ???
    @Override
    public MedicalRecordResponseDTO getRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord not found with id: " + id));
        return toDTO(record);
    }


    // ✅ ENTITY -> DTO DÖNÜŞÜMÜ
    private MedicalRecordResponseDTO toDTO(MedicalRecord record) {
        MedicalRecordResponseDTO dto = new MedicalRecordResponseDTO();
        dto.setId(record.getId());
        dto.setPatient(patientService.convertToDTO(record.getPatient()));
        dto.setDoctor(doctorService.convertToDTO(record.getDoctor()));
        dto.setDiagnosis(record.getDiagnosis());
        dto.setPrescription(record.getPrescription());
        dto.setNotes(record.getNotes());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        return dto;
    }

    // ✅ DTO -> ENTITY DÖNÜŞÜMÜ
    private MedicalRecord toEntity(MedicalRecordRequestDTO dto) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patientService.getPatientEntityById(dto.getPatientId()));
        record.setDoctor(doctorService.getDoctorEntityById(dto.getDoctorId()));
        record.setDiagnosis(dto.getDiagnosis());
        record.setPrescription(dto.getPrescription());
        record.setNotes(dto.getNotes());
        return record;
    }
}
