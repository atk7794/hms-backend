package com.example.hms.service;

import com.example.hms.model.User;

public interface EmailVerificationService {
    String createVerificationToken(User user);
    boolean validateVerificationToken(String token);
    void verifyUser(String token);
}
