package com.example.hms.controller;

import com.example.hms.dto.request.PrescriptionRequestDTO;
import com.example.hms.dto.response.PrescriptionResponseDTO;
import com.example.hms.service.PrescriptionService;
import com.example.hms.service.UserActionLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:4200")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final UserActionLogService userActionLogService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  UserActionLogService userActionLogService) {
        this.prescriptionService = prescriptionService;
        this.userActionLogService = userActionLogService;
    }

    // 🩺 1️⃣ Doktor veya Admin — yeni reçete oluşturur
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO> createPrescription(
            @Valid @RequestBody PrescriptionRequestDTO request) {
        PrescriptionResponseDTO response = prescriptionService.createPrescription(request);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "CREATE_PRESCRIPTION",
                "Created prescription (ID: " + response.getId() + ") for patientId " + request.getPatientId());

        return ResponseEntity.ok(response);
    }

    // 📝 2️⃣ Doktor — kendi oluşturduğu reçeteleri listeler
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorId(doctorId));
    }

    // 👤 3️⃣ Hasta — kendi reçetelerini listeler
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    // 🧾 4️⃣ Tekil reçete görüntüleme (doktor, hasta veya admin)
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    // ✏️ 5️⃣ Doktor veya Admin — reçete güncelleme
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO> updatePrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionRequestDTO request) {

        PrescriptionResponseDTO response = prescriptionService.updatePrescription(id, request);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "UPDATE_PRESCRIPTION",
                "Updated prescription (ID: " + id + ") for patientId " + request.getPatientId());

        return ResponseEntity.ok(response);
    }

    // 🗑️ 6️⃣ Admin veya Doktor — reçete silme
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        // önce mevcut reçeteyi al
        PrescriptionResponseDTO response = prescriptionService.getPrescriptionById(id);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "DELETE_PRESCRIPTION",
                "Deleted prescription (ID: " + id + ") for patient " +
                        response.getPatientName() + " (ID: " + response.getPatientId() + ")");

        // sonra sil
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    // 🌍 7️⃣ Admin — tüm reçeteleri listeler
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PrescriptionResponseDTO>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

}
