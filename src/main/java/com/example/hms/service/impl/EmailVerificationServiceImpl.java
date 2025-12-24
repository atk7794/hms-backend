package com.example.hms.service.impl;

import com.example.hms.model.EmailVerificationToken;
import com.example.hms.model.User;
import com.example.hms.repository.EmailVerificationTokenRepository;
import com.example.hms.repository.UserRepository;
import com.example.hms.service.EmailVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationServiceImpl(EmailVerificationTokenRepository tokenRepository,
                                        UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public String createVerificationToken(User user) {
        // var olan token'ı temizle
        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken t = new EmailVerificationToken();
        t.setToken(token);
        t.setUser(user);
        t.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24 saat geçerli
        t.setUsed(false);
        tokenRepository.save(t);
        return token;
    }

    @Override
    public boolean validateVerificationToken(String token) {
        EmailVerificationToken t = tokenRepository.findByToken(token).orElse(null);
        if (t == null) return false;
        if (t.isUsed()) return false;
        if (t.getExpiryDate().isBefore(LocalDateTime.now())) return false;
        return true;
    }

    @Override
    @Transactional
    public void verifyUser(String token) {
        EmailVerificationToken t = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (t.isUsed() || t.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token invalid or expired");
        }

        User user = t.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        t.setUsed(true);
        tokenRepository.save(t);
    }
}
