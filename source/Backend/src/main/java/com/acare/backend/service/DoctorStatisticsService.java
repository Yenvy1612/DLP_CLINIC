package com.acare.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.acare.backend.dto.appointment.statistics.DoctorDailyVisitPointResponse;
import com.acare.backend.dto.appointment.statistics.DoctorPatientAppointmentItemResponse;
import com.acare.backend.dto.appointment.statistics.DoctorPatientAppointmentPageResponse;
import com.acare.backend.dto.appointment.statistics.DoctorPatientSummaryRowResponse;
import com.acare.backend.dto.appointment.statistics.DoctorStatisticsDashboardResponse;
import com.acare.backend.dto.appointment.statistics.DoctorStatisticsFilterRequest;
import com.acare.backend.dto.appointment.statistics.DoctorStatisticsPeriodType;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.AppointmentStatus;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.UserRepository;
import com.acare.backend.repository.projection.DoctorDailyDoneCountProjection;
import com.acare.backend.repository.projection.DoctorPatientServiceCountProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorStatisticsService {

    private static final int DEFAULT_HISTORY_PAGE_SIZE = 5;
    private static final int MAX_HISTORY_PAGE_SIZE = 20;

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public DoctorStatisticsDashboardResponse getDashboard(String doctorEmail, DoctorStatisticsFilterRequest request) {
        Long doctorId = resolveDoctorId(doctorEmail);
        DateRange range = resolveDateRange(request);
        String normalizedKeyword = normalizeKeyword(request != null ? request.getKeyword() : null);

        long uniquePatients = appointmentRepository.countDistinctDonePatientsByDoctorInRange(
                doctorId,
                AppointmentStatus.DONE,
                range.fromDateTime(),
                range.toExclusiveDateTime());

        long doneAppointments = appointmentRepository.countDoneAppointmentsByDoctorInRange(
                doctorId,
                AppointmentStatus.DONE,
                range.fromDateTime(),
                range.toExclusiveDateTime());

        List<DoctorDailyVisitPointResponse> dailyVisits = buildDailyVisitSeries(doctorId, range);
        List<DoctorPatientSummaryRowResponse> patientRows = buildPatientRows(doctorId, range, normalizedKeyword);

        return DoctorStatisticsDashboardResponse.builder()
                .periodType(range.periodType().name())
                .fromDate(range.fromDate())
                .toDate(range.toDate())
                .uniquePatientCount(uniquePatients)
                .doneAppointmentCount(doneAppointments)
                .dailyVisits(dailyVisits)
                .patientRows(patientRows)
                .build();
    }

    public DoctorPatientAppointmentPageResponse getPatientAppointments(
            String doctorEmail,
            Long patientId,
            DoctorStatisticsFilterRequest request,
            int page,
            int size) {

        if (patientId == null) {
            throw new BadRequestException("Patient id khong hop le");
        }

        Long doctorId = resolveDoctorId(doctorEmail);
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        if (patient.getRole() != UserRole.PATIENT) {
            throw new BadRequestException("Nguoi duoc chon khong phai benh nhan");
        }

        DateRange range = resolveDateRange(request);
        Pageable pageable = PageRequest.of(normalizePage(page), normalizeSize(size));

        Page<DoctorPatientAppointmentItemResponse> mappedPage = appointmentRepository
                .findDoctorPatientDoneAppointmentsInRange(
                        doctorId,
                        patientId,
                        AppointmentStatus.DONE,
                        range.fromDateTime(),
                        range.toExclusiveDateTime(),
                        pageable)
                .map(projection -> DoctorPatientAppointmentItemResponse.builder()
                        .appointmentId(projection.getAppointmentId())
                        .appointmentCode(projection.getAppointmentCode())
                        .startTime(projection.getStartTime())
                        .endTime(projection.getEndTime())
                        .status(projection.getStatus() != null ? projection.getStatus().name() : null)
                        .serviceName(projection.getServiceName())
                        .reason(projection.getReason())
                        .note(projection.getNote())
                        .build());

        return DoctorPatientAppointmentPageResponse.builder()
                .patientId(patientId)
                .patientName(patient.getFullName())
                .periodType(range.periodType().name())
                .fromDate(range.fromDate())
                .toDate(range.toDate())
                .page(mappedPage.getNumber())
                .size(mappedPage.getSize())
                .totalPages(mappedPage.getTotalPages())
                .totalElements(mappedPage.getTotalElements())
                .items(mappedPage.getContent())
                .build();
    }

    private List<DoctorDailyVisitPointResponse> buildDailyVisitSeries(Long doctorId, DateRange range) {
        List<DoctorDailyDoneCountProjection> groupedRows = appointmentRepository.countDoneAppointmentsByDoctorGroupedByDate(
                doctorId,
                AppointmentStatus.DONE,
                range.fromDateTime(),
                range.toExclusiveDateTime());

        Map<LocalDate, Long> countByDay = new TreeMap<>();
        for (DoctorDailyDoneCountProjection row : groupedRows) {
            if (row.getVisitDate() == null) {
                continue;
            }
            Long totalCount = row.getTotalCount();
            countByDay.put(row.getVisitDate(), totalCount != null ? totalCount : 0L);
        }

        List<DoctorDailyVisitPointResponse> result = new ArrayList<>();
        LocalDate cursor = range.fromDate();
        while (!cursor.isAfter(range.toDate())) {
            result.add(DoctorDailyVisitPointResponse.builder()
                    .date(cursor)
                    .doneAppointments(countByDay.getOrDefault(cursor, 0L))
                    .build());
            cursor = cursor.plusDays(1);
        }

        return result;
    }

    private List<DoctorPatientSummaryRowResponse> buildPatientRows(Long doctorId, DateRange range, String keyword) {
        List<DoctorPatientServiceCountProjection> rows = appointmentRepository.summarizeDoctorPatientsByServiceInRange(
                doctorId,
                AppointmentStatus.DONE,
                range.fromDateTime(),
                range.toExclusiveDateTime(),
                keyword);

        Map<Long, PatientAccumulator> accumulatorByPatient = new TreeMap<>();
        for (DoctorPatientServiceCountProjection row : rows) {
            if (row.getPatientId() == null) {
                continue;
            }
            PatientAccumulator accumulator = accumulatorByPatient.computeIfAbsent(
                    row.getPatientId(),
                    ignored -> new PatientAccumulator(row.getPatientId(), row.getPatientName()));

            Long totalCount = row.getTotalCount();
            accumulator.totalDone += totalCount != null ? totalCount : 0L;
            if (row.getServiceName() != null && !row.getServiceName().isBlank()) {
                accumulator.services.add(row.getServiceName());
            }
        }

        return accumulatorByPatient.values().stream()
                .map(acc -> DoctorPatientSummaryRowResponse.builder()
                        .patientId(acc.patientId)
                        .patientName(acc.patientName)
                        .doneAppointments(acc.totalDone)
                        .services(List.copyOf(acc.services))
                        .build())
                .sorted((left, right) -> {
                    int compareByCount = Long.compare(right.getDoneAppointments(), left.getDoneAppointments());
                    if (compareByCount != 0) {
                        return compareByCount;
                    }
                    String leftName = left.getPatientName() == null ? "" : left.getPatientName();
                    String rightName = right.getPatientName() == null ? "" : right.getPatientName();
                    return leftName.compareToIgnoreCase(rightName);
                })
                .toList();
    }

    private int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_HISTORY_PAGE_SIZE;
        }
        return Math.min(size, MAX_HISTORY_PAGE_SIZE);
    }

    private Long resolveDoctorId(String doctorEmail) {
        if (doctorEmail == null || doctorEmail.isBlank()) {
            throw new BadRequestException("Thong tin dang nhap khong hop le");
        }

        User doctor = userRepository.findByEmailIgnoreCase(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tai khoan bac si"));

        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new BadRequestException("Tai khoan hien tai khong phai bac si");
        }

        return doctor.getId();
    }

    private DateRange resolveDateRange(DoctorStatisticsFilterRequest request) {
        DoctorStatisticsPeriodType periodType = DoctorStatisticsPeriodType.from(
                request != null ? request.getPeriodType() : null);

        LocalDate now = LocalDate.now();
        Integer selectedYear = request != null ? request.getYear() : null;
        int year = Objects.requireNonNullElse(selectedYear, now.getYear());

        LocalDate fromDate;
        LocalDate toDate;

        switch (periodType) {
            case DAY -> {
                LocalDate day = request != null && request.getDate() != null ? request.getDate() : now;
                fromDate = day;
                toDate = day;
            }
            case QUARTER -> {
                Integer selectedQuarter = request != null ? request.getQuarter() : null;
                int quarter = Objects.requireNonNullElse(selectedQuarter, ((now.getMonthValue() - 1) / 3) + 1);
                if (quarter < 1 || quarter > 4) {
                    throw new BadRequestException("Quy thong ke khong hop le");
                }
                int firstMonth = (quarter - 1) * 3 + 1;
                fromDate = LocalDate.of(year, firstMonth, 1);
                toDate = fromDate.plusMonths(3).minusDays(1);
            }
            case MONTH -> {
                Integer selectedMonth = request != null ? request.getMonth() : null;
                int month = Objects.requireNonNullElse(selectedMonth, now.getMonthValue());
                if (month < 1 || month > 12) {
                    throw new BadRequestException("Thang thong ke khong hop le");
                }
                fromDate = LocalDate.of(year, month, 1);
                toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
            }
            default -> throw new BadRequestException("Loai bo loc thong ke khong hop le");
        }

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toExclusiveDateTime = toDate.plusDays(1).atTime(LocalTime.MIDNIGHT);

        return new DateRange(periodType, fromDate, toDate, fromDateTime, toExclusiveDateTime);
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return keyword.trim();
    }

    private record DateRange(
            DoctorStatisticsPeriodType periodType,
            LocalDate fromDate,
            LocalDate toDate,
            LocalDateTime fromDateTime,
            LocalDateTime toExclusiveDateTime) {
    }

    private static final class PatientAccumulator {
        private final Long patientId;
        private final String patientName;
        private long totalDone;
        private final LinkedHashSet<String> services;

        private PatientAccumulator(Long patientId, String patientName) {
            this.patientId = patientId;
            this.patientName = patientName;
            this.totalDone = 0;
            this.services = new LinkedHashSet<>();
        }
    }
}
