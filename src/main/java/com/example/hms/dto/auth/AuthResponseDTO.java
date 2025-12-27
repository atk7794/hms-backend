package com.example.hms.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {
    private Long id;
    private String email;
    private String role;
    private String message;

    private Long patientId;

    private String token;


    // 🔧 Jackson için gerekli default constructor
    public AuthResponseDTO() {
    }

    public AuthResponseDTO(Long id, String email, String role, String message) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.message = message;
    }

    public AuthResponseDTO(Long id, String email, String role, String message, Long patientId) {
        this(id, email, role, message);
        this.patientId = patientId;
    }

    // ✅ Yeni constructor (token dahil)
    public AuthResponseDTO(Long id, String email, String role, String message, Long patientId, String token) {
        this(id, email, role, message, patientId);
        this.token = token;
    }

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }


    // getters/setters for all ==> USER ID VE PATIENT ID SORUNU İÇİN
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }


    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
