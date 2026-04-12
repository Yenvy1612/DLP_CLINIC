package com.acare.backend.entity;

import java.time.LocalDateTime;

import com.acare.backend.entity.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
/* Tạo ràng buộc tương ứng với trong db */
@Table(name = "appointments", uniqueConstraints = {
        @UniqueConstraint(name = "uq_appointments_code", columnNames = { "appointment_code" }),
        @UniqueConstraint(name = "uq_appt_doctor_time", columnNames = { "doctor_id", "start_time" })
})

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_code", length = 30, nullable = false, unique = true)
    private String appointmentCode;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private User patient;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private User doctor;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(length = 255)
    private String reason;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Appointment prepareForCreate(String generatedCode) {
        String resolvedCode = this.appointmentCode;
        if (resolvedCode == null || resolvedCode.isBlank()) {
            resolvedCode = generatedCode;
        }

        return this.toBuilder()
                .appointmentCode(resolvedCode)
                .status(this.status == null ? AppointmentStatus.PENDING : this.status)
                .endTime(this.endTime == null && this.startTime != null ? this.startTime.plusMinutes(30) : this.endTime)
                .build();
    }

    public Appointment ensureEndTimeDefault() {
        if (this.endTime != null || this.startTime == null) {
            return this;
        }

        return this.toBuilder()
                .endTime(this.startTime.plusMinutes(30))
                .build();
    }

    public Appointment withStatus(AppointmentStatus nextStatus) {
        return this.toBuilder()
                .status(nextStatus)
                .build();
    }

    public Appointment mergeFrom(Appointment update) {
        if (update == null) {
            return this;
        }

        return this.toBuilder()
                .patientId(update.getPatientId() != null ? update.getPatientId() : this.patientId)
                .doctorId(update.getDoctorId() != null ? update.getDoctorId() : this.doctorId)
                .serviceId(update.getServiceId() != null ? update.getServiceId() : this.serviceId)
                .startTime(update.getStartTime() != null ? update.getStartTime() : this.startTime)
                .endTime(update.getEndTime() != null ? update.getEndTime() : this.endTime)
                .reason(update.getReason() != null ? update.getReason() : this.reason)
                .note(update.getNote() != null ? update.getNote() : this.note)
                .status(update.getStatus() != null ? update.getStatus() : this.status)
                .build();
    }
}
