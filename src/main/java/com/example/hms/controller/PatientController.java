package com.example.hms.controller;

import com.example.hms.dto.request.PatientRequestDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.Patient;
import com.example.hms.repository.PatientRepository;
import com.example.hms.service.PatientService;
import com.example.hms.service.UserActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    // Tüm hastaları listele
    @GetMapping
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    // Yeni hasta ekle
    @PostMapping
    public PatientResponseDTO createPatient(@RequestBody PatientRequestDTO patientDTO) {
        PatientResponseDTO response = patientService.createPatient(patientDTO);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "CREATE_PATIENT",
                "Created patient (ID: " + response.getId() + ") " +
                        response.getFirstName() + " " + response.getLastName());

        return response;
    }

    // ID ile hasta sil
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        PatientResponseDTO response = patientService.getPatientById(id); // önce mevcut veriyi al

        // User action log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "DELETE_PATIENT",
                "Deleted patient (ID: " + id + ") " +
                        response.getFirstName() + " " + response.getLastName());

        patientService.deletePatient(id); // sonra sil
    }

    // ID ile hasta güncelle
    @PutMapping("/{id}")
    public PatientResponseDTO updatePatient(@PathVariable Long id, @RequestBody PatientRequestDTO patientDTO) {
        PatientResponseDTO response = patientService.updatePatient(id, patientDTO);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "UPDATE_PATIENT",
                "Updated patient (ID: " + id + ") " +
                        response.getFirstName() + " " + response.getLastName());

        return response;
    }

    // ID ile tek bir hasta getir
    @GetMapping("/{id}")
    public PatientResponseDTO getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/user/{userId}")
    public PatientResponseDTO getPatientByUserId(@PathVariable Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with userId: " + userId));

        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setGender(patient.getGender());
        dto.setBirthDate(patient.getBirthDate());
        return dto;
    }
}
