package com.acare.backend.dto.medicalrecord;

import java.time.LocalDate;

import com.acare.backend.entity.MedicalRecord;

import lombok.Data;

@Data
public class MedicalRecordRequest {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String chiefComplaint;
    private String diagnosis;
    private String treatmentPlan;
    private String clinicalNotes;
    private LocalDate followUpDate;

    public MedicalRecord toEntity() {
        return MedicalRecord.builder()
                .appointmentId(appointmentId)
                .patientId(patientId)
                .doctorId(doctorId)
                .chiefComplaint(chiefComplaint)
                .diagnosis(diagnosis)
                .treatmentPlan(treatmentPlan)
                .clinicalNotes(clinicalNotes)
                .followUpDate(followUpDate)
                .build();
    }
}
