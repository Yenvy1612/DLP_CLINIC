package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dlp.DlpProtected;
import com.acare.backend.dto.medicalrecord.MedicalRecordRequest;
import com.acare.backend.dto.medicalrecord.MedicalRecordResponse;
import com.acare.backend.dto.medicalrecord.MedicalRecordServiceItemRequest;
import com.acare.backend.dto.medicalrecord.MedicalRecordServiceItemResponse;
import com.acare.backend.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createRecord(@RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(medicalRecordService.toResponse(medicalRecordService.createMedicalRecord(request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateRecord(@PathVariable Long id, @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(medicalRecordService.toResponse(medicalRecordService.updateMedicalRecord(id, request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @DlpProtected(action = "DOWNLOAD")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.toResponse(medicalRecordService.getById(id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @DlpProtected(action = "DOWNLOAD")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordResponse> getByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(medicalRecordService.toResponse(medicalRecordService.getByAppointmentId(appointmentId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @DlpProtected(action = "DOWNLOAD")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatientId(patientId).stream().map(medicalRecordService::toResponse).toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getByDoctorId(doctorId).stream().map(medicalRecordService::toResponse).toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/{recordId}/services")
    public ResponseEntity<MedicalRecordServiceItemResponse> addServiceToRecord(
            @PathVariable Long recordId,
            @RequestBody MedicalRecordServiceItemRequest request) {
        return ResponseEntity.ok(MedicalRecordServiceItemResponse.from(medicalRecordService.addServiceToRecord(recordId, request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/{recordId}/services")
    public ResponseEntity<List<MedicalRecordServiceItemResponse>> getServicesByRecordId(@PathVariable Long recordId) {
        return ResponseEntity.ok(medicalRecordService.getServicesByRecordId(recordId).stream().map(MedicalRecordServiceItemResponse::from).toList());
    }
}
