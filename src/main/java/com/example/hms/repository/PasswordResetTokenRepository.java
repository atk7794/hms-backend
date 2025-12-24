package com.example.hms.repository;

import com.example.hms.model.PasswordResetToken;
import com.example.hms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    // Token ile bul
    Optional<PasswordResetToken> findByToken(String token);

    // Kullanıcıya ait ve henüz kullanılmamış token bul
    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

    // Token silme opsiyonel, temizleme için
    @Transactional
    @Modifying
    void deleteByUser(User user);
}
