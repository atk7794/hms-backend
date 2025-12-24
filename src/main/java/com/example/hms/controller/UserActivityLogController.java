package com.example.hms.controller;

import com.example.hms.model.UserActivityLog;
import com.example.hms.service.UserActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-activity-logs")
public class UserActivityLogController {

    private final UserActivityLogService userActivityLogService;

    public UserActivityLogController(UserActivityLogService userActivityLogService) {
        this.userActivityLogService = userActivityLogService;
    }

    // Tüm logları getir
    @GetMapping
    public List<UserActivityLog> getAllLogs() {
        return userActivityLogService.getAllLogs();
    }
}
