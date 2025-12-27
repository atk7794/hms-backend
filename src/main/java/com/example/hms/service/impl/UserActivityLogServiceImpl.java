package com.example.hms.service.impl;

import com.example.hms.model.UserActivityLog;
import com.example.hms.repository.UserActivityLogRepository;
import com.example.hms.service.UserActivityLogService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
// changed

@Service
public class UserActivityLogServiceImpl implements UserActivityLogService {

    private final UserActivityLogRepository logRepository;

    public UserActivityLogServiceImpl(UserActivityLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void logLogin(String username, String ipAddress) {
        UserActivityLog log = new UserActivityLog(username, LocalDateTime.now(), ipAddress);
        logRepository.save(log);
    }

    @Override
    public void logLogout(String username) {
        logRepository.findFirstByUsernameAndLogoutAtIsNullOrderByLoginAtDesc(username)
                .ifPresent(log -> {
                    log.setLogoutAt(LocalDateTime.now());
                    log.setDurationSeconds(Duration.between(log.getLoginAt(), log.getLogoutAt()).getSeconds());
                    logRepository.save(log);
                });
    }

    @Override
    public List<UserActivityLog> getAllLogs() {
        return logRepository.findAll();
    }

    @Override
    public List<UserActivityLog> getOpenSessions(String username) {
        return logRepository.findByUsernameAndLogoutAtIsNull(username);
    }

    @Override
    public void updateSession(UserActivityLog session) {
        logRepository.save(session);
    }

}
