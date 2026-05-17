package com.acare.backend.dto.medicalrecord;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.acare.backend.entity.MedicalRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private Long id;
    private String recordCode;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String chiefComplaint;
    private String diagnosis;
    private String treatmentPlan;
    private String clinicalNotes;
    private LocalDate followUpDate;
    private String doctorName;
    private String patientFullName;
    private String patientEmail;
    private String patientPhone;
    private String patientIdNumber;
    private String bloodType;
    private String insuranceNumber;
    private String allergies;
    private String chronicConditions;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MedicalRecordResponse from(MedicalRecord record) {
        if (record == null) {
            return null;
        }

        return MedicalRecordResponse.builder()
                .id(record.getId())
                .recordCode(record.getRecordCode())
                .appointmentId(record.getAppointmentId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .chiefComplaint(record.getChiefComplaint())
                .diagnosis(record.getDiagnosis())
                .treatmentPlan(record.getTreatmentPlan())
                .clinicalNotes(record.getClinicalNotes())
                .followUpDate(record.getFollowUpDate())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
