package com.example.hms.repository;

import com.example.hms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);

    // Patient ID üzerinden filtreleme
    List<Appointment> findByPatientId(Long patientId);


    // Eğer istersen status ile filtreleme için ek metodlar
    List<Appointment> findByPatientIdAndStatus(Long patientId, String status);

    List<Appointment> findByDoctorIdAndStatus(Long patientId, String status);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.patient p " +
            "JOIN FETCH a.doctor d " +
            "WHERE a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findAppointmentsWithinRangeWithRelations(LocalDateTime start, LocalDateTime end);


}
