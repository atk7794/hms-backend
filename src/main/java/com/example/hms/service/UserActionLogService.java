package com.example.hms.service;

import com.example.hms.model.UserActionLog;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActionLogService {
    void logAction(String username, String action, String description);

    List<UserActionLog> getAllActions();
    List<UserActionLog> getAllLogs();
    List<UserActionLog> getLogsByUsername(String username);
    List<UserActionLog> getLogsByActionType(String actionType);
    List<UserActionLog> getLogsBetween(LocalDateTime start, LocalDateTime end);
}
