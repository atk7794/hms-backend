package com.example.hms.service.impl;

import com.example.hms.model.EmailLog;
import com.example.hms.repository.EmailLogRepository;
import com.example.hms.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final EmailLogRepository emailLogRepository;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from}")
    private String fromEmail;

    public EmailServiceImpl(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {

        EmailLog log = new EmailLog();
        log.setRecipient(to);
        log.setSubject(subject);
        log.setSentAt(LocalDateTime.now());

        try {
            String jsonBody = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "%s",
                  "html": "%s"
                }
                """.formatted(
                    fromEmail,
                    to,
                    escapeJson(subject),
                    escapeJson(text)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.setSuccess(true);
                logger.info("✅ Email sent successfully via Resend -> {}", to);
            } else {
                log.setSuccess(false);
                log.setErrorMessage(response.body());
                logger.warn("❌ Resend email failed: {}", response.body());
            }

        } catch (Exception e) {
            log.setSuccess(false);
            log.setErrorMessage(e.getMessage());
            logger.error("❌ Critical error while sending email", e);
        }

        emailLogRepository.save(log);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
