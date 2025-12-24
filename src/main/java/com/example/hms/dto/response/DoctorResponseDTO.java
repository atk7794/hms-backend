package com.example.hms.dto.response;

public class DoctorResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialty;

    private Long userId; // 🔹 EKLENDİ

    public DoctorResponseDTO() {}

    public DoctorResponseDTO(Long id, String firstName, String lastName, String specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
    }

    public DoctorResponseDTO(Long id, String firstName, String lastName, String specialty, Long userId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }


    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

