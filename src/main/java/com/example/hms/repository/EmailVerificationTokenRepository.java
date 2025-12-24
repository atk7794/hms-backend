package com.example.hms.repository;

import com.example.hms.model.EmailVerificationToken;
import com.example.hms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);

    @Transactional
    @Modifying
    void deleteByUser(User user);
}
