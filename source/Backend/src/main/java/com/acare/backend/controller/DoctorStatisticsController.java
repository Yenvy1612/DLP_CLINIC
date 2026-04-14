package com.acare.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.appointment.statistics.DoctorPatientAppointmentPageResponse;
import com.acare.backend.dto.appointment.statistics.DoctorStatisticsDashboardResponse;
import com.acare.backend.dto.appointment.statistics.DoctorStatisticsFilterRequest;
import com.acare.backend.service.DoctorStatisticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorStatisticsController {

    private final DoctorStatisticsService doctorStatisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DoctorStatisticsDashboardResponse> getDashboard(
            Authentication authentication,
            @ModelAttribute DoctorStatisticsFilterRequest filterRequest) {
        return ResponseEntity.ok(doctorStatisticsService.getDashboard(authentication.getName(), filterRequest));
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<DoctorPatientAppointmentPageResponse> getPatientAppointments(
            Authentication authentication,
            @PathVariable Long patientId,
            @ModelAttribute DoctorStatisticsFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(
                doctorStatisticsService.getPatientAppointments(
                        authentication.getName(),
                        patientId,
                        filterRequest,
                        page,
                        size));
    }
}
