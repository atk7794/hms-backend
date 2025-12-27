package com.example.hms.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.hms.model.Appointment;
import com.example.hms.repository.AppointmentRepository;
import com.example.hms.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    public ReminderService(AppointmentRepository appointmentRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    // 🔹 Her sabah 08:00'de çalışır (Türkiye saatine göre)
    @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Istanbul")
    public void sendAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findAppointmentsWithinRangeWithRelations(start, end);

        if (appointments.isEmpty()) {
            // System.out.println("ℹ️ Yarın için randevu bulunamadı.");
            logger.info("No patient appointments found for tomorrow.");
            return;
        }

        for (Appointment appointment : appointments) {
            try {
                var patient = appointment.getPatient();
                var doctor = appointment.getDoctor();

                String email = patient.getUser().getEmail();
                String subject = "📅 Randevu Hatırlatma";
                String message = String.format(
                        "<h3>Sayın %s %s,</h3>" +
                                "<p>Yarın bir randevunuz bulunmaktadır.</p>" +
                                "<p><b>Tarih:</b> %s</p>" +
                                "<p><b>Doktor:</b> %s %s (%s)</p>" +
                                "<br><p>Sağlıklı günler dileriz 💙</p>",
                        patient.getFirstName(),
                        patient.getLastName(),
                        appointment.getAppointmentDate(),
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getSpecialty()
                );

                emailService.sendEmail(email, subject, message);
                // System.out.println("✅ Hatırlatma maili gönderildi -> " + email);
                logger.info("Patient reminder email sent -> {}", email);

            } catch (Exception e) {
                // System.err.println("⚠️ Hatırlatma maili gönderilemedi: " + e.getMessage());
                logger.warn(
                        "Failed to send patient reminder email | appointmentId={} reason={}",
                        appointment.getId(),
                        e.getMessage()
                );
                logger.error("Reminder email error", e);
            }
        }
    }

    // 🔹 Doktorlara ertesi günkü randevuların özetini gönder
    @Scheduled(cron = "0 5 8 * * *", zone = "Europe/Istanbul")
    public void sendDoctorReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findAppointmentsWithinRangeWithRelations(start, end);

        if (appointments.isEmpty()) {
            // System.out.println("ℹ️ Doktorlar için yarın randevu bulunamadı.");
            logger.info("No doctor appointments found for tomorrow.");
            return;
        }

        Map<com.example.hms.model.Doctor, List<Appointment>> appointmentsByDoctor =
                appointments.stream().collect(Collectors.groupingBy(Appointment::getDoctor));

        for (var entry : appointmentsByDoctor.entrySet()) {
            var doctor = entry.getKey();
            var doctorAppointments = entry.getValue();

            String email = doctor.getUser().getEmail();
            String subject = "📅 Yarınki Randevularınız";
            StringBuilder message = new StringBuilder();

            message.append(String.format(
                    "<h3>Sayın Dr. %s %s,</h3>" +
                            "<p>Yarın için planlanmış randevularınız aşağıdadır:</p><ul>",
                    doctor.getFirstName(),
                    doctor.getLastName()
            ));

            for (Appointment a : doctorAppointments) {
                message.append(String.format(
                        "<li><b>%s %s</b> — %s</li>",
                        a.getPatient().getFirstName(),
                        a.getPatient().getLastName(),
                        a.getAppointmentDate().toLocalTime()
                ));
            }

            message.append("</ul><br><p>Kolaylıklar dileriz 💙</p>");

            try {
                emailService.sendEmail(email, subject, message.toString());
                // System.out.println("✅ Doktora hatırlatma maili gönderildi -> " + email);
                logger.info("Doctor reminder email sent -> {}", email);
            } catch (Exception e) {
                // System.err.println("⚠️ Doktora mail gönderilemedi: " + e.getMessage());
                logger.warn(
                        "Failed to send doctor reminder email | doctorId={} reason={}",
                        doctor.getId(),
                        e.getMessage()
                );
                logger.error("Doctor reminder email error", e);
            }
        }
    }
}
