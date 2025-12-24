package com.example.hms.service.impl;

import com.example.hms.model.UserActionLog;
import com.example.hms.repository.UserActionLogRepository;
import com.example.hms.service.UserActionLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActionLogServiceImpl implements UserActionLogService {

    private final UserActionLogRepository userActionLogRepository;

    public UserActionLogServiceImpl(UserActionLogRepository userActionLogRepository) {
        this.userActionLogRepository = userActionLogRepository;
    }

    @Override
    public void logAction(String username, String action, String description) {
        UserActionLog log = new UserActionLog(username, action, description);
        userActionLogRepository.save(log);
    }

    @Override
    public List<UserActionLog> getAllActions() {
        return userActionLogRepository.findAll();
    }

    @Override
    public List<UserActionLog> getAllLogs() {
        return userActionLogRepository.findAll();
    }

    @Override
    public List<UserActionLog> getLogsByUsername(String username) {
        return userActionLogRepository.findByUsername(username);
    }

    @Override
    public List<UserActionLog> getLogsByActionType(String actionType) {
        return userActionLogRepository.findByAction(actionType);
    }

    @Override
    public List<UserActionLog> getLogsBetween(LocalDateTime start, LocalDateTime end) {
        return userActionLogRepository.findByCreatedAtBetween(start, end);
    }
}
