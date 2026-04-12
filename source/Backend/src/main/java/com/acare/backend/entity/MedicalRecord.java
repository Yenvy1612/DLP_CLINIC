package com.acare.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_code", nullable = false, unique = true, length = 30)
    private String recordCode;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

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

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

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

    public MedicalRecord prepareForCreate(Long resolvedPatientId, Long resolvedDoctorId, String generatedCode) {
        String resolvedCode = this.recordCode;
        if (resolvedCode == null || resolvedCode.isBlank()) {
            resolvedCode = generatedCode;
        }

        return this.toBuilder()
                .patientId(resolvedPatientId)
                .doctorId(resolvedDoctorId)
                .recordCode(resolvedCode)
                .build();
    }

    public MedicalRecord mergeFrom(MedicalRecord update) {
        if (update == null) {
            return this;
        }

        return this.toBuilder()
                .chiefComplaint(update.getChiefComplaint() != null ? update.getChiefComplaint() : this.chiefComplaint)
                .diagnosis(update.getDiagnosis() != null ? update.getDiagnosis() : this.diagnosis)
                .treatmentPlan(update.getTreatmentPlan() != null ? update.getTreatmentPlan() : this.treatmentPlan)
                .clinicalNotes(update.getClinicalNotes() != null ? update.getClinicalNotes() : this.clinicalNotes)
                .followUpDate(update.getFollowUpDate() != null ? update.getFollowUpDate() : this.followUpDate)
                .build();
    }
}
