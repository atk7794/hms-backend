package com.example.hms.service;

import com.example.hms.model.User;

public interface PasswordResetService {

    // Şifre sıfırlama token oluştur
    String createPasswordResetToken(User user);

    // Token ile kullanıcıyı doğrula
    boolean validatePasswordResetToken(String token);

    // Yeni şifre belirle
    void resetPassword(String token, String newPassword);

}
