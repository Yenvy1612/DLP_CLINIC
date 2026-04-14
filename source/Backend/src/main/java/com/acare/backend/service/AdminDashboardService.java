package com.acare.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.acare.backend.dto.admin.AdminDashboardSummaryResponse;
import com.acare.backend.entity.enums.AppointmentStatus;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.UserRepository;
import com.acare.backend.repository.projection.TopNameCountProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private static final String EMPTY_LABEL = "Chưa có dữ liệu";

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminDashboardSummaryResponse getCurrentMonthSummary() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.of(23, 59, 59));

        long userCount = userRepository.count();

        TopNameCountProjection topDoctor = appointmentRepository
                .findTopDoctorsByStatusAndStartTimeBetween(AppointmentStatus.DONE, from, to, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        TopNameCountProjection topService = appointmentRepository
                .findTopServicesByStatusAndStartTimeBetween(AppointmentStatus.DONE, from, to, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        BigDecimal monthRevenue = appointmentRepository
                .sumRevenueByStatusAndStartTimeBetween(AppointmentStatus.DONE, from, to);
        if (monthRevenue == null) {
            monthRevenue = BigDecimal.ZERO;
        }

        return AdminDashboardSummaryResponse.builder()
                .userCount(userCount)
                .topDoctorName(topDoctor != null ? topDoctor.getName() : EMPTY_LABEL)
                                .topDoctorDoneCount(resolveCount(topDoctor))
                .topServiceName(topService != null ? topService.getName() : EMPTY_LABEL)
                                .topServiceDoneCount(resolveCount(topService))
                .monthRevenue(monthRevenue)
                .month(today.getMonthValue())
                .year(today.getYear())
                .build();
    }

        private long resolveCount(TopNameCountProjection projection) {
                if (projection == null || projection.getTotalCount() == null) {
                        return 0L;
                }
                return projection.getTotalCount();
        }
}
