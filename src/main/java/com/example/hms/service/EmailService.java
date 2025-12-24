package com.example.hms.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
