package com.example.hms.service.impl;

import com.example.hms.dto.request.PrescriptionRequestDTO;
import com.example.hms.dto.response.PrescriptionResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.MedicalRecord;
import com.example.hms.model.Prescription;
import com.example.hms.model.Patient;
import com.example.hms.model.Doctor;
import com.example.hms.repository.MedicalRecordRepository;
import com.example.hms.repository.PrescriptionRepository;
import com.example.hms.service.PrescriptionService;
import com.example.hms.service.PatientService;
import com.example.hms.service.DoctorService;
import com.example.hms.service.EmailService;
import com.example.hms.service.UserActionLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final EmailService emailService;
    private final UserActionLogService userActionLogService;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   PatientService patientService,
                                   DoctorService doctorService,
                                   MedicalRecordRepository medicalRecordRepository,
                                   EmailService emailService,
                                   UserActionLogService userActionLogService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.medicalRecordRepository = medicalRecordRepository;
        this.emailService = emailService;
        this.userActionLogService = userActionLogService;
    }


    // Service impl
    @Override
    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public PrescriptionResponseDTO getPrescriptionById(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));
        return toResponseDTO(p);
    }

    @Override
    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {
        Patient patient = patientService.getPatientEntityById(dto.getPatientId());
        Doctor doctor = doctorService.getDoctorEntityById(dto.getDoctorId());

        Prescription p = new Prescription();
        p.setPatient(patient);
        p.setDoctor(doctor);
        p.setMedication(dto.getMedication());
        p.setDosage(dto.getDosage());
        p.setInstructions(dto.getInstructions());

        // Eğer prescriptionCode yoksa otomatik üret
        if (dto.getPrescriptionCode() == null || dto.getPrescriptionCode().isEmpty()) {
            String generatedCode = "RX-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"))
                    + "-" + (int)(Math.random() * 10000);
            p.setPrescriptionCode(generatedCode);
        } else {
            p.setPrescriptionCode(dto.getPrescriptionCode());
        }

        if (dto.getMedicalRecordId() != null) {
            MedicalRecord mr = medicalRecordRepository.findById(dto.getMedicalRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "MedicalRecord not found with id: " + dto.getMedicalRecordId()));
            p.setMedicalRecord(mr);
        }

        Prescription saved = prescriptionRepository.save(p);

        // ✅ Yeni reçete oluşturulduğunda e-posta bildirimi
        // ✅ HTML e-posta gönderimi, DB’den alınan prescriptionCode ile
        sendPrescriptionEmail(patient, doctor, saved, true);

        // ✅ UserActionLog kaydı
        String logDesc = "Dr. " + doctor.getFirstName() + " " + doctor.getLastName()
                + " created prescription for " + patient.getFirstName() + " " + patient.getLastName();
        if (doctor.getUser() != null) {
            userActionLogService.logAction(doctor.getUser().getEmail(), "CREATE_PRESCRIPTION", logDesc);
        }

        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public PrescriptionResponseDTO updatePrescription(Long id, PrescriptionRequestDTO dto) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        Patient patient = patientService.getPatientEntityById(dto.getPatientId());
        Doctor doctor = doctorService.getDoctorEntityById(dto.getDoctorId());

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setMedication(dto.getMedication());
        existing.setDosage(dto.getDosage());
        existing.setInstructions(dto.getInstructions());

        // Burayı ekle
        // PrescriptionCode update ediliyorsa
        if (dto.getPrescriptionCode() != null && !dto.getPrescriptionCode().isEmpty()) {
            existing.setPrescriptionCode(dto.getPrescriptionCode());
        } // yoksa mevcut kalır, duplicate riskini önler

        if (dto.getMedicalRecordId() != null) {
            MedicalRecord mr = medicalRecordRepository.findById(dto.getMedicalRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "MedicalRecord not found with id: " + dto.getMedicalRecordId()));
            existing.setMedicalRecord(mr);
        } else {
            existing.setMedicalRecord(null);
        }

        Prescription updated = prescriptionRepository.save(existing);

        // ✅ DB entity üzerinden mail gönderiyoruz
        sendPrescriptionEmail(patient, doctor, updated, false);

        // ✅ UserActionLog kaydı
        String logDesc = "Dr. " + doctor.getFirstName() + " " + doctor.getLastName()
                + " updated prescription for " + patient.getFirstName() + " " + patient.getLastName();
        if (doctor.getUser() != null) {
            userActionLogService.logAction(doctor.getUser().getEmail(), "UPDATE_PRESCRIPTION", logDesc);
        }

        return toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deletePrescription(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + id));

        Patient patient = p.getPatient();
        Doctor doctor = p.getDoctor();

        prescriptionRepository.deleteById(id);

        // ✅ Reçete silindiğinde mail gönder
        try {
            String subject = "Reçeteniz Silindi";
            StringBuilder body = new StringBuilder();
            body.append("Sayın ").append(patient.getFirstName()).append(" ").append(patient.getLastName()).append(",<br><br>");
            body.append("Doktorunuz Dr. ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName())
                    .append(" tarafından reçeteniz silinmiştir.<br><br>");
            body.append("<b>Reçete Kodu:</b> ").append(p.getPrescriptionCode()).append("<br><br>");
            body.append("Sisteme giriş yaparak detaylı bilgileri görüntüleyebilirsiniz.<br><br>");
            body.append("Sağlıklı günler dileriz,<br>Hospital Management System");

            if (patient.getUser() != null && patient.getUser().getEmail() != null) {
                emailService.sendEmail(patient.getUser().getEmail(), subject, body.toString());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Prescription delete email failed: " + e.getMessage());
        }

        // ✅ UserActionLog
        if (doctor.getUser() != null) {
            String logDesc = "Dr. " + doctor.getFirstName() + " " + doctor.getLastName()
                    + " deleted prescription for " + patient.getFirstName() + " " + patient.getLastName();
            userActionLogService.logAction(doctor.getUser().getEmail(), "DELETE_PRESCRIPTION", logDesc);
        }
    }


    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // 📨 Ortak e-posta metodu
    private void sendPrescriptionEmail(Patient patient, Doctor doctor, Prescription saved, boolean isNew) {
        try {
            String subject = isNew ? "Yeni Reçeteniz Oluşturuldu" : "Reçeteniz Güncellendi";

            StringBuilder body = new StringBuilder();
            body.append("Sayın ").append(patient.getFirstName()).append(" ").append(patient.getLastName()).append(",<br><br>");
            body.append("Doktorunuz Dr. ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName())
                    .append(isNew ? " tarafından yeni bir reçete oluşturulmuştur." : " tarafından reçeteniz güncellenmiştir.").append("<br><br>");

            body.append("<b>Reçete Kodu:</b> ").append(saved.getPrescriptionCode()).append("<br><br>");

            body.append("<table border='1' cellpadding='5' cellspacing='0'>");
            body.append("<tr><th>İlaç</th><th>Dozaj</th><th>Talimatlar</th></tr>");
            body.append("<tr>");
            body.append("<td>").append(saved.getMedication()).append("</td>");
            body.append("<td>").append(saved.getDosage()).append("</td>");
            body.append("<td>").append(saved.getInstructions()).append("</td>");
            body.append("</tr>");
            body.append("</table><br>");

            body.append("Sisteme giriş yaparak detaylı reçete bilgilerini görüntüleyebilirsiniz.<br><br>");
            body.append("Sağlıklı günler dileriz,<br>Hospital Management System");

            if (patient.getUser() != null && patient.getUser().getEmail() != null) {
                emailService.sendEmail(patient.getUser().getEmail(), subject, body.toString());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Prescription email notification failed: " + e.getMessage());
        }
    }


//    // 📨 ESKİ : Ortak e-posta metodu
//    private void sendPrescriptionEmail(Patient patient, Doctor doctor, PrescriptionRequestDTO dto, boolean isNew) {
//        try {
//            String subject = isNew ? "Yeni Reçeteniz Oluşturuldu" : "Reçeteniz Güncellendi";
//            String body = "Sayın " + patient.getFirstName() + " " + patient.getLastName() + ",\n\n"
//                    + "Doktorunuz Dr. " + doctor.getFirstName() + " " + doctor.getLastName()
//                    + (isNew ? " tarafından yeni bir reçete oluşturulmuştur." : " tarafından reçeteniz güncellenmiştir.") + "\n\n"
//                    + "İlaç: " + dto.getMedication() + "\n"
//                    + "Dozaj: " + dto.getDosage() + "\n"
//                    + "Açıklamalar: " + dto.getInstructions() + "\n\n"
//                    + "Sisteme giriş yaparak detaylı reçete bilgilerini görüntüleyebilirsiniz.\n\n"
//                    + "Sağlıklı günler dileriz,\nHospital Management System";
//
//            if (patient.getUser() != null && patient.getUser().getEmail() != null) {
//                emailService.sendEmail(patient.getUser().getEmail(), subject, body);
//            }
//        } catch (Exception e) {
//            System.err.println("⚠️ Prescription email notification failed: " + e.getMessage());
//        }
//    }


    // Helper: entity -> dto
    private PrescriptionResponseDTO toResponseDTO(Prescription p) {
        PrescriptionResponseDTO dto = new PrescriptionResponseDTO();
        dto.setId(p.getId());
        dto.setPrescriptionCode(p.getPrescriptionCode() != null ? p.getPrescriptionCode() : ""); // ← ekle

        if (p.getPatient() != null) {
            dto.setPatientId(p.getPatient().getId());
            dto.setPatientName(p.getPatient().getFirstName() + " " + p.getPatient().getLastName());
        }
        if (p.getDoctor() != null) {
            dto.setDoctorId(p.getDoctor().getId());
            dto.setDoctorName(p.getDoctor().getFirstName() + " " + p.getDoctor().getLastName());
        }
        if (p.getMedicalRecord() != null) {
            dto.setMedicalRecordId(p.getMedicalRecord().getId());
        }
        dto.setMedication(p.getMedication());
        dto.setDosage(p.getDosage());
        dto.setInstructions(p.getInstructions());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}
