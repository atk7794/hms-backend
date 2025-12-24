package com.example.hms.repository;

import com.example.hms.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    // Kullanıcıya ait açık oturumu bulmak için
    Optional<UserActivityLog> findFirstByUsernameAndLogoutAtIsNullOrderByLoginAtDesc(String username);

    // 🔹 Kullanıcıya ait açık tüm oturumlar
    List<UserActivityLog> findByUsernameAndLogoutAtIsNull(String username);

}
