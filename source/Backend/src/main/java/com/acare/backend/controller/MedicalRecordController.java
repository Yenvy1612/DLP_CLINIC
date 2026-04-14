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
        return ResponseEntity.ok(MedicalRecordResponse.from(medicalRecordService.createMedicalRecord(request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateRecord(@PathVariable Long id, @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(MedicalRecordResponse.from(medicalRecordService.updateMedicalRecord(id, request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(MedicalRecordResponse.from(medicalRecordService.getById(id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordResponse> getByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(MedicalRecordResponse.from(medicalRecordService.getByAppointmentId(appointmentId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordService.getByPatientId(patientId).stream().map(MedicalRecordResponse::from).toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(medicalRecordService.getByDoctorId(doctorId).stream().map(MedicalRecordResponse::from).toList());
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
