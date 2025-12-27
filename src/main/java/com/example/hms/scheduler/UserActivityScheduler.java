package com.example.hms.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.hms.model.UserActivityLog;
import com.example.hms.repository.UserActivityLogRepository;
import com.example.hms.service.UserActivityLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class UserActivityScheduler {

    @Value("${app.timezone:Europe/Istanbul}")
    private String timezone;
    private final UserActivityLogService userActivityLogService;
    private final UserActivityLogRepository userActivityLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserActivityScheduler.class);

    public UserActivityScheduler(UserActivityLogService userActivityLogService,
                                 UserActivityLogRepository userActivityLogRepository) {
        this.userActivityLogService = userActivityLogService;
        this.userActivityLogRepository = userActivityLogRepository;
    }

    // Her 5 dakikada bir çalışacak
    @Scheduled(cron = "0 */5 * * * *")
    public void autoLogoutInactiveUsers() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(timezone));

        // Tüm login kayıtlarını al
        List<UserActivityLog> activeLogs = userActivityLogRepository.findAll();

        for (UserActivityLog log : activeLogs) {
            // Logout zamanı boş ise ve login üzerinden 12 saat geçmişse
            if (log.getLogoutAt() == null && log.getLoginAt().isBefore(now.minusHours(12))) {
                log.setLogoutAt(now);
                userActivityLogRepository.save(log);
                // System.out.println("⚠️ Auto-logout: " + log.getUsername() + " at " + now);
                logger.warn("Auto-logout: {} at {}", log.getUsername(), now);
            }
        }
    }
}
