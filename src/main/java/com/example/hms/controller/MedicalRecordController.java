package com.example.hms.controller;

import com.example.hms.dto.request.MedicalRecordRequestDTO;
import com.example.hms.dto.response.MedicalRecordResponseDTO;
import com.example.hms.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllRecords());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByPatientId(patientId));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createRecord(@Valid @RequestBody MedicalRecordRequestDTO dto) {
        return ResponseEntity.ok(medicalRecordService.createRecord(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> updateRecord(@PathVariable Long id,
                                                                 @Valid @RequestBody MedicalRecordRequestDTO dto) {
        return ResponseEntity.ok(medicalRecordService.updateRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Tek bir tıbbi kaydı ID ile getir
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    // ✅ Doktora göre kayıtları getir
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getRecordsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getRecordsByDoctorId(doctorId));
    }
}
