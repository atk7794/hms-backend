package com.example.hms.controller;

import com.example.hms.dto.request.DoctorRequestDTO;
import com.example.hms.dto.response.DoctorResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.Doctor;
import com.example.hms.repository.DoctorRepository;
import com.example.hms.service.DoctorService;
import com.example.hms.service.UserActionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserActionLogService userActionLogService;

    // Tüm doktorları listele
    @GetMapping
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    // ID ile doktor getir
    @GetMapping("/{id}")
    public DoctorResponseDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    // Yeni doktor ekle
    @PostMapping
    public DoctorResponseDTO createDoctor(@RequestBody DoctorRequestDTO doctorRequestDTO) {
        DoctorResponseDTO response = doctorService.createDoctor(doctorRequestDTO);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "CREATE_DOCTOR",
                "Created doctor (ID: " + response.getId() + ") " +
                        response.getFirstName() + " " + response.getLastName());

        return response;
    }

    // ID ile doktor güncelle
    @PutMapping("/{id}")
    public DoctorResponseDTO updateDoctor(@PathVariable Long id, @RequestBody DoctorRequestDTO doctorRequestDTO) {
        DoctorResponseDTO response = doctorService.updateDoctor(id, doctorRequestDTO);

        // User Action Log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "UPDATE_DOCTOR",
                "Updated doctor (ID: " + id + ") " +
                        response.getFirstName() + " " + response.getLastName());

        return response;
    }

    // ID ile doktor sil
    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        DoctorResponseDTO response = doctorService.getDoctorById(id); // önce mevcut veriyi al

        // User action log
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userActionLogService.logAction(username, "DELETE_DOCTOR",
                "Deleted doctor (ID: " + id + ") " +
                        response.getFirstName() + " " + response.getLastName());

        doctorService.deleteDoctor(id); // sonra sil
    }

    // Tüm specialty’leri getir
    @GetMapping("/specialities")
    public List<String> getAllSpecialities() {
        return doctorService.getAllSpecialities();
    }

    // Belirli specialty’ye göre doktorları getir
    @GetMapping(params = "speciality")
    public List<DoctorResponseDTO> getDoctorsBySpeciality(@RequestParam String speciality) {
        return doctorService.getDoctorsBySpeciality(speciality);
    }

    @GetMapping("/user/{userId}")
    public DoctorResponseDTO getDoctorByUserId(@PathVariable Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with userId: " + userId));

        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setSpecialty(doctor.getSpecialty());
        return dto;
    }
}
