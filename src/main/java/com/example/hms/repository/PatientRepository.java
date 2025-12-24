package com.example.hms.repository;

import com.example.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // JpaRepository, CRUD ve sorgu metodlarını otomatik sağlar

    //User üzerinden patient bulma ihtimaline karşı bir metod istersek
    Optional<Patient> findByUserId(Long userId);

    // 🔽 Bunu ekliyoruz:
    boolean existsByUserId(Long userId);

}
