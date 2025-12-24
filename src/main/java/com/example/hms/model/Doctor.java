package com.example.hms.model;

import jakarta.persistence.*;
//import lombok.Data;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String specialty; // uzmanlık alanı


    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Appointment> appointments = new java.util.ArrayList<>();



    // Constructors
    public Doctor() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public java.util.List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(java.util.List<Appointment> appointments) { this.appointments = appointments; }

    public User getUser() { return this.user; }
    public void setUser(User savedUser) { this.user = savedUser; }

}
