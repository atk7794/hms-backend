package com.example.hms.controller;

import com.example.hms.model.UserActionLog;
import com.example.hms.service.UserActionLogService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user-actions")
public class UserActionLogController {

    private final UserActionLogService userActionLogService;

    public UserActionLogController(UserActionLogService userActionLogService) {
        this.userActionLogService = userActionLogService;
    }

    // 🔹 Tüm logları listele
    @GetMapping
    public List<UserActionLog> getAllLogs() {
        return userActionLogService.getAllLogs();
    }

    // 🔹 Kullanıcıya göre filtreleme
    @GetMapping("/user/{username}")
    public List<UserActionLog> getLogsByUsername(@PathVariable String username) {
        return userActionLogService.getLogsByUsername(username);
    }

    // 🔹 İşlem türüne göre filtreleme
    @GetMapping("/action/{actionType}")
    public List<UserActionLog> getLogsByAction(@PathVariable String actionType) {
        return userActionLogService.getLogsByActionType(actionType);
    }

    // 🔹 Tarih aralığına göre filtreleme
    @GetMapping("/between")
    public List<UserActionLog> getLogsBetween(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return userActionLogService.getLogsBetween(startDate, endDate);
    }
}
