package com.example.hms.service.impl;

import com.example.hms.dto.request.PatientRequestDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.Patient;
import com.example.hms.model.User;
import com.example.hms.repository.PatientRepository;
import com.example.hms.repository.UserRepository;
import com.example.hms.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;


    // Tüm hastaları getir
    @Override
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ID ile hastayı getir
    @Override
    public PatientResponseDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return convertToDTO(patient);
    }

    // User üzerinden patient getirme
    public PatientResponseDTO getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user ID: " + userId));
        return convertToDTO(patient);
    }

    // Yeni hasta ekle
    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientDTO) {
        Patient patient = convertToEntity(patientDTO);

        // User set et
        User user = userRepository.findById(patientDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        patient.setUser(user);

//        // User entity'sine de patient set et
//        user.setPatient(patient);
//
//        // 🔹 EKLENDİ: user tablosunu DB'ye yaz
//        userRepository.save(user);

        Patient saved = patientRepository.save(patient);

        // Persist işlemini garantiye almak için flush et (isteğe bağlı ama bazen gerekebiliyor)
        patientRepository.flush();

        return convertToDTO(saved);
    }



    // ID ile hasta güncelle
    @Override
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setBirthDate(dto.getBirthDate());
        existing.setGender(dto.getGender());

        if (existing.getUser() != null) {
            User user = existing.getUser();
            user.setEmail(dto.getEmail());

            // ✅ Şifreyi hashle (güncellemede)
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
            }

            userRepository.save(user);
        }

        Patient updated = patientRepository.save(existing);
        return convertToDTO(updated);
    }

    // ID ile hasta sil
    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        // Eğer User varsa önce sil
        if (patient.getUser() != null) {
            userRepository.delete(patient.getUser());
        }

        patientRepository.delete(patient);
    }


    public void updatePatientByUserId(PatientRequestDTO dto) {
        Optional<Patient> existing = patientRepository.findByUserId(dto.getUserId());
        if (existing.isPresent()) {
            Patient patient = existing.get();
            patient.setFirstName(dto.getFirstName());
            patient.setLastName(dto.getLastName());
            patient.setGender(dto.getGender());
            patient.setBirthDate(dto.getBirthDate());
            patientRepository.save(patient);
        }
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return patientRepository.existsByUserId(userId);
    }


    @Override
    public Patient getPatientEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
    }



    // Entity -> ResponseDTO
    public PatientResponseDTO convertToDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());

        // 🔹 userId ekle
        if (patient.getUser() != null) {
            dto.setUserId(patient.getUser().getId());
        }

        return dto;
    }


    // RequestDTO -> Entity
    private Patient convertToEntity(PatientRequestDTO dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        return patient;
    }
}
