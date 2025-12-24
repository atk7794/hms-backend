package com.example.hms.service.impl;

import com.example.hms.dto.request.AppointmentRequestDTO;
import com.example.hms.dto.response.AppointmentResponseDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.dto.response.DoctorResponseDTO;
import com.example.hms.exception.ResourceNotFoundException;
import com.example.hms.model.Appointment;
import com.example.hms.model.Patient;
import com.example.hms.model.Doctor;
import com.example.hms.repository.AppointmentRepository;
import com.example.hms.repository.PatientRepository;
import com.example.hms.repository.DoctorRepository;
import com.example.hms.service.AppointmentService;
import com.example.hms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return convertToResponseDTO(appointment);
    }

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStatus(dto.getStatus());

        Appointment saved = appointmentRepository.save(appointment);

        try {
            String appointmentDateStr = dto.getAppointmentDate().toString(); // Tarih ve saat

            // Hasta maili
            String patientEmail = patient.getUser().getEmail();
            String subjectPatient = "Randevunuz Oluşturuldu";
            String messagePatient = String.format(
                    "<h3>Sayın %s %s,</h3>" +
                            "<p>Randevunuz başarıyla oluşturulmuştur.</p>" +
                            "<p><b>Tarih & Saat:</b> %s</p>" +
                            "<p><b>Doktor:</b> %s %s (%s)</p>" +
                            "<br><p>Sağlıklı günler dileriz.</p>",
                    patient.getFirstName(),
                    patient.getLastName(),
                    appointmentDateStr,
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getSpecialty()
            );
            emailService.sendEmail(patientEmail, subjectPatient, messagePatient);

            // Doktor maili
            String doctorEmail = doctor.getUser().getEmail();
            String subjectDoctor = "Yeni Randevu Oluşturuldu";
            String messageDoctor = String.format(
                    "<h3>Sayın Dr. %s %s,</h3>" +
                            "<p>Yeni bir randevunuz oluşturuldu.</p>" +
                            "<p><b>Hasta:</b> %s %s</p>" +
                            "<p><b>Tarih & Saat:</b> %s</p>",
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    appointmentDateStr
            );
            emailService.sendEmail(doctorEmail, subjectDoctor, messageDoctor);

        } catch (Exception e) {
            System.err.println("⚠️ Randevu maili gönderilemedi: " + e.getMessage());
        }

        return convertToResponseDTO(saved);
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setStatus(dto.getStatus());

        Appointment updated = appointmentRepository.save(existing);

        try {
            String dateStr = dto.getAppointmentDate().toString();

            String patientEmail = patient.getUser().getEmail();
            String subjectPatient = "Randevunuz Güncellendi";
            String messagePatient = String.format(
                    "<h3>Sayın %s %s,</h3>" +
                            "<p>Randevunuz güncellenmiştir.</p>" +
                            "<p><b>Yeni Tarih & Saat:</b> %s</p>" +
                            "<p><b>Doktor:</b> %s %s (%s)</p>",
                    patient.getFirstName(),
                    patient.getLastName(),
                    dateStr,
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    doctor.getSpecialty()
            );
            emailService.sendEmail(patientEmail, subjectPatient, messagePatient);

            // Doktor maili
            String doctorEmail = doctor.getUser().getEmail();
            String subjectDoctor = "Randevu Güncellendi";
            String messageDoctor = String.format(
                    "<h3>Sayın Dr. %s %s,</h3>" +
                            "<p>%s %s için randevu güncellenmiştir.</p>" +
                            "<p><b>Tarih & Saat:</b> %s</p>",
                    doctor.getFirstName(),
                    doctor.getLastName(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    dateStr
            );
            emailService.sendEmail(doctorEmail, subjectDoctor, messageDoctor);

        } catch (Exception e) {
            System.err.println("⚠️ Güncelleme maili gönderilemedi: " + e.getMessage());
        }

        return convertToResponseDTO(updated);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        try {
            String dateStr = appointment.getAppointmentDate().toString();
            String patientEmail = appointment.getPatient().getUser().getEmail();
            String doctorEmail = appointment.getDoctor().getUser().getEmail();
            String doctorName = appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName();

            // Hasta maili
            String subjectPatient = "Randevunuz İptal Edildi";
            String messagePatient = String.format(
                    "<h3>Sayın %s %s,</h3>" +
                            "<p>%s doktoru ile olan randevunuz iptal edilmiştir.</p>" +
                            "<p><b>İptal Tarihi & Saat:</b> %s</p>",
                    appointment.getPatient().getFirstName(),
                    appointment.getPatient().getLastName(),
                    doctorName,
                    dateStr
            );
            emailService.sendEmail(patientEmail, subjectPatient, messagePatient);

            // Doktor maili
            String subjectDoctor = "Randevu İptal Edildi";
            String messageDoctor = String.format(
                    "<h3>Sayın Dr. %s %s,</h3>" +
                            "<p>%s %s isimli hastanın randevusu iptal edilmiştir.</p>" +
                            "<p><b>İptal Tarihi & Saat:</b> %s</p>",
                    appointment.getDoctor().getFirstName(),
                    appointment.getDoctor().getLastName(),
                    appointment.getPatient().getFirstName(),
                    appointment.getPatient().getLastName(),
                    dateStr
            );
            emailService.sendEmail(doctorEmail, subjectDoctor, messageDoctor);

        } catch (Exception e) {
            System.err.println("⚠️ İptal maili gönderilemedi: " + e.getMessage());
        }

        appointmentRepository.delete(appointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Entity -> ResponseDTO
    // Entity -> ResponseDTO
    private AppointmentResponseDTO convertToResponseDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());

        // 🔹 PatientDTO doldur
        Patient p = appointment.getPatient();
        if (p != null) {
            PatientResponseDTO patientDTO = new PatientResponseDTO();
            patientDTO.setId(p.getId());
            patientDTO.setFirstName(p.getFirstName());
            patientDTO.setLastName(p.getLastName());
            patientDTO.setBirthDate(p.getBirthDate());
            patientDTO.setGender(p.getGender());

            // 🟩 Eksik olan satır: userId set et
            if (p.getUser() != null) {
                patientDTO.setUserId(p.getUser().getId());
            }

            dto.setPatient(patientDTO);
        }

        // 🔹 DoctorDTO doldur
        Doctor d = appointment.getDoctor();
        if (d != null) {
            DoctorResponseDTO doctorDTO = new DoctorResponseDTO();
            doctorDTO.setId(d.getId());
            doctorDTO.setFirstName(d.getFirstName());
            doctorDTO.setLastName(d.getLastName());
            doctorDTO.setSpecialty(d.getSpecialty());

            // 🟩 Eksik olan satır: userId set et
            // Doctor'un userId'si burada set edilmeli
            if (d.getUser() != null) {
                doctorDTO.setUserId(d.getUser().getId());
            }

            dto.setDoctor(doctorDTO);
        }
        return dto;
    }
}
