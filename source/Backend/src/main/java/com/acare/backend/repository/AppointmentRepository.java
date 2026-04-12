package com.acare.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
        List<Appointment> findByStatusAndPatientId(AppointmentStatus status, Long patientId);
        List<Appointment> findByStatusAndDoctorId(AppointmentStatus status, Long doctorId);
    List<Appointment> findByStartTimeBetween(LocalDateTime from, LocalDateTime to);
        List<Appointment> findByStartTimeBetweenAndStatus(LocalDateTime from, LocalDateTime to, AppointmentStatus status);
        List<Appointment> findByStartTimeBetweenAndStatusAndDoctorId(LocalDateTime from, LocalDateTime to, AppointmentStatus status, Long doctorId);

        @Query("""
                        select count(a) > 0
                        from Appointment a
                        where a.doctorId = :doctorId
                            and a.status in :activeStatuses
                            and :startTime < a.endTime
                            and :endTime > a.startTime
                        """)
        boolean existsDoctorConflict(@Param("doctorId") Long doctorId,
                                                                 @Param("startTime") LocalDateTime startTime,
                                                                 @Param("endTime") LocalDateTime endTime,
                                                                 @Param("activeStatuses") List<AppointmentStatus> activeStatuses);

        @Query("""
                        select count(a) > 0
                        from Appointment a
                        where a.id <> :appointmentId
                            and a.doctorId = :doctorId
                            and a.status in :activeStatuses
                            and :startTime < a.endTime
                            and :endTime > a.startTime
                        """)
        boolean existsDoctorConflictExcludingId(@Param("appointmentId") Long appointmentId,
                                                                                        @Param("doctorId") Long doctorId,
                                                                                        @Param("startTime") LocalDateTime startTime,
                                                                                        @Param("endTime") LocalDateTime endTime,
                                                                                        @Param("activeStatuses") List<AppointmentStatus> activeStatuses);

}
