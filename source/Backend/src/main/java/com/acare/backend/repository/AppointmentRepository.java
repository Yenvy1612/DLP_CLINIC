package com.acare.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatusAndPatientId(String status, Long patientId);
    List<Appointment> findByStatusAndDoctorId(String status, Long doctorId);
    List<Appointment> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
    List<Appointment> findByStartTimeBetweenAndStatus(LocalDateTime from, LocalDateTime to, String status);
    List<Appointment> findByStartTimeBetweenAndStatusAndDoctorId(LocalDateTime from, LocalDateTime to, String status, Long doctorId);
    boolean existsByStartTime(LocalDateTime startTime);
    boolean existsByDoctorIdAndStartTime(Long doctorId, LocalDateTime startTime);
    boolean existsByRoomIdAndStartTime(Long roomId, LocalDateTime startTime);
}
