package com.example.hms.service;

import com.example.hms.model.UserActivityLog;

import java.util.List;

public interface UserActivityLogService {
    void logLogin(String username, String ipAddress);
    void logLogout(String username);
    List<UserActivityLog> getAllLogs();

    // 🔹 Yeni metodlar
    List<UserActivityLog> getOpenSessions(String username);
    void updateSession(UserActivityLog session);

}
