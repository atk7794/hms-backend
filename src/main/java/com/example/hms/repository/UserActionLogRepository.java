package com.example.hms.repository;

import com.example.hms.model.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
    List<UserActionLog> findByUsername(String username);
    List<UserActionLog> findByAction(String action);
    List<UserActionLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
