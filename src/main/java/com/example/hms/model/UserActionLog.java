package com.example.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_action_log")
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;      // İşlemi yapan kullanıcı
    private String action;        // Örn: "CREATE_PRESCRIPTION", "UPDATE_PRESCRIPTION"
    private String description;   // Detaylı açıklama, örn: "Dr. Ahmet Yılmaz created prescription for Ali Demir"
    private LocalDateTime createdAt;

    public UserActionLog() {}

    public UserActionLog(String username, String action, String description) {
        this.username = username;
        this.action = action;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
