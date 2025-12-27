package com.example.hms.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.hms.model.EmailLog;
import com.example.hms.repository.EmailLogRepository;
import com.example.hms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender, EmailLogRepository emailLogRepository) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        EmailLog log = new EmailLog();
        log.setRecipient(to);
        log.setSubject(subject);
        log.setSentAt(LocalDateTime.now());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail); // kendi mail adresin
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true => HTML olarak gönder

            mailSender.send(message);
            log.setSuccess(true);
            // System.out.println("✅ Mail başarıyla gönderildi -> " + to);
            logger.info("Email sent successfully -> {}", to);

        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getMessage());
            // System.err.println("❌ Mail gönderim hatası: " + e.getMessage());
            logger.warn("Failed to send email -> {}", e.getMessage());
            logger.error("Critical error occurred while sending email", e);
        }

        // Her durumda logu DB'ye yaz
        emailLogRepository.save(log);
    }
}
