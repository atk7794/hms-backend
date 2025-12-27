package com.example.hms.repository;

import com.example.hms.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Şimdilik ekstra bir query gerek yok, temel CRUD JpaRepository ile yeterli

    // İleride “uzmanlık alanına göre doktor listele” gibi bir sorgu eklemek istersek
    List<Doctor> findBySpecialty(String specialty);

    @Query("SELECT DISTINCT d.specialty FROM Doctor d")
    List<String> findAllSpecialties();

    Optional<Doctor> findByUserId(Long userId);

}
