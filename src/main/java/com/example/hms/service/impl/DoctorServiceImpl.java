package com.example.hms.service.impl;

import org.springframework.beans.factory.annotation.Value;
import com.example.hms.dto.request.DoctorRequestDTO;
import com.example.hms.dto.response.DoctorResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.Doctor;
import com.example.hms.model.User;
import com.example.hms.repository.DoctorRepository;
import com.example.hms.repository.UserRepository;
import com.example.hms.service.DoctorService;
import com.example.hms.service.EmailService;
import com.example.hms.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // Tüm doktorları getir
    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ID ile tek bir doktoru getir
    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        return convertToDTO(doctor);
    }

    // Yeni doktor ekle
    @Override
    public DoctorResponseDTO createDoctor(DoctorRequestDTO doctorDTO) {
        // 1️⃣ User oluştur
        User user = new User();
        user.setEmail(doctorDTO.getEmail());

        // ✅ Şifreyi hashle
        String hashedPassword = BCrypt.hashpw(doctorDTO.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        user.setRole("DOCTOR");

        // 2️⃣ Doctor oluştur
        Doctor doctor = new Doctor();
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setSpecialty(doctorDTO.getSpecialty());

        // 3️⃣ User ↔ Doctor ilişkisini kur
        doctor.setUser(user);
        user.setDoctor(doctor);

        // 4️⃣ User kaydet (cascade ile doctor da kaydedilecek)
        User savedUser = userRepository.save(user);

        // 4️⃣ 🔹 Email doğrulama token ve mail gönder
        String token = emailVerificationService.createVerificationToken(savedUser);
        String link = frontendUrl + "/verify-email?token=" + token;

        String htmlContent = "<!DOCTYPE html>"
                + "<html><body>"
                + "<h2>HMS E-posta Doğrulama</h2>"
                + "<p>Merhaba <strong>" + savedUser.getEmail() + "</strong>,</p>"
                + "<p>Hesabınızı aktifleştirmek için aşağıdaki butona tıklayın:</p>"
                + "<a href='" + link + "' "
                + "style='display:inline-block;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;"
                + "border-radius:5px;'>E-postayı Doğrula</a>"
                + "<p>Eğer bu isteği siz yapmadıysanız, bu mesajı yok sayabilirsiniz.</p>"
                + "<p>HMS Destek Ekibi</p>"
                + "</body></html>";

        emailService.sendEmail(savedUser.getEmail(), "HMS E-posta Doğrulama", htmlContent);

        // 5️⃣ DTO döndür
        return convertToDTO(savedUser.getDoctor());
    }

    // ID ile doktor güncelle
    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorDTO) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        // Doctor bilgilerini güncelle
        existingDoctor.setFirstName(doctorDTO.getFirstName());
        existingDoctor.setLastName(doctorDTO.getLastName());
        existingDoctor.setSpecialty(doctorDTO.getSpecialty());

        // 🔹 User bilgilerini güncelle
        if (existingDoctor.getUser() != null) {
            User user = existingDoctor.getUser();
            user.setEmail(doctorDTO.getEmail());

            // ✅ Güncellemede de hash uygula
            if (doctorDTO.getPassword() != null && !doctorDTO.getPassword().isBlank()) {
                String hashedPassword = BCrypt.hashpw(doctorDTO.getPassword(), BCrypt.gensalt());
                user.setPassword(hashedPassword);
            }

            userRepository.save(user);
        }

        Doctor updated = doctorRepository.save(existingDoctor);
        return convertToDTO(updated);
    }

    // ID ile doktor sil
    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        // 🔹 Eğer User varsa sil
        if (doctor.getUser() != null) {
            userRepository.delete(doctor.getUser());
        }

        doctorRepository.delete(doctor);
    }

    @Override
    public List<String> getAllSpecialities() {
        return doctorRepository.findAllSpecialties();
    }

    @Override
    public List<DoctorResponseDTO> getDoctorsBySpeciality(String speciality) {
        return doctorRepository.findBySpecialty(speciality)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DoctorResponseDTO getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user ID: " + userId));
        return convertToDTO(doctor);
    }

    @Override
    public Doctor getDoctorEntityById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
    }


    // Entity → ResponseDTO
    public DoctorResponseDTO convertToDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setSpecialty(doctor.getSpecialty());
        if (doctor.getUser() != null) {
            dto.setUserId(doctor.getUser().getId());
        }
        return dto;
    }

    // RequestDTO → Entity
    private Doctor convertToEntity(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setSpecialty(dto.getSpecialty());
        return doctor;
    }
}
