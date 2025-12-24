package com.example.hms.controller;

import com.example.hms.model.EmailLog;
import com.example.hms.repository.EmailLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-logs")
public class EmailLogController {

    private final EmailLogRepository emailLogRepository;

    public EmailLogController(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @GetMapping
    public List<EmailLog> getAllLogs() {
        return emailLogRepository.findAll();
    }
}
