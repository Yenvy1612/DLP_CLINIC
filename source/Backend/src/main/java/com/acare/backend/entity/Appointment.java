package com.acare.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
/* Tạo ràng buộc tương ứng với trong db */
@Table(name = "appointments", uniqueConstraints = {
        @UniqueConstraint(name = "uq_appt_doctor_time", columnNames = { "doctor_id", "start_time" }),
        @UniqueConstraint(name = "uq_appt_room_time", columnNames = { "room_id", "start_time" })
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(length = 12, nullable = false)
    private String status = "PENDING";

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
