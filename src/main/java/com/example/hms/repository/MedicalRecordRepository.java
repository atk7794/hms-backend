package com.example.hms.repository;

import com.example.hms.model.MedicalRecord;
import com.example.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatient(Patient patient);
    // ✅ Doktora göre kayıtları getir
    List<MedicalRecord> findByDoctorId(Long doctorId);
}
