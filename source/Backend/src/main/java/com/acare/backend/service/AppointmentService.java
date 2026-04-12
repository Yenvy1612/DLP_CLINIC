package com.acare.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.appointment.AppointmentAvailabilityOption;
import com.acare.backend.dto.appointment.AppointmentCreateRequest;
import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.DoctorProfile;
import com.acare.backend.entity.Specialty;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.AppointmentStatus;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.exception.ConflictException;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.DoctorProfileRepository;
import com.acare.backend.repository.ServiceRepository;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final LocalTime BUSINESS_START = LocalTime.of(8, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(17, 0);

    private static final List<AppointmentStatus> ACTIVE_SCHEDULE_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED
    );

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final SpecialtyService specialtyService;
    private final ActivityLogService activityLogService;

    @Transactional
    public ApiResponse<Appointment> createAppointment(AppointmentCreateRequest request) {
        if (request == null) {
            throw new BadRequestException("Du lieu dat lich khong hop le");
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .serviceId(request.getServiceId())
                .startTime(request.getStartTime())
                .reason(request.getReason())
                .note(request.getNote())
                .build();

        Appointment saved = createAppointmentInternal(appointment, request.getServiceId());
        return ApiResponse.created("Da dat lich hen", saved);
    }

    @Transactional
    public ApiResponse<Appointment> createAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new BadRequestException("Du lieu dat lich khong hop le");
        }

        Long serviceId = appointment.getServiceId();
        if (serviceId == null) {
            serviceId = extractServiceIdFromLegacyNote(appointment.getNote());
        }

        Appointment saved = createAppointmentInternal(appointment, serviceId);
        return ApiResponse.created("Da dat lich hen", saved);
    }

    private Appointment createAppointmentInternal(Appointment appointment, Long serviceId) {
        if (appointment == null) {
            throw new BadRequestException("Du lieu dat lich khong hop le");
        }
        if (serviceId == null) {
            throw new BadRequestException("Vui long chon dich vu");
        }
        if (appointment.getStartTime() == null) {
            throw new BadRequestException("Start time khong duoc de trong");
        }

        validatePatientOnly(appointment.getPatientId());

        com.acare.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        if (Boolean.FALSE.equals(service.getActive())) {
            throw new BadRequestException("Dich vu dang tam ngung hoat dong");
        }

        if (service.getDurationMin() == null || service.getDurationMin() <= 0) {
            throw new BadRequestException("Dich vu chua duoc cau hinh thoi gian kham hop le");
        }

        Appointment working = appointment.toBuilder()
                .serviceId(serviceId)
                .endTime(appointment.getStartTime().plusMinutes(service.getDurationMin()))
                .build();

        ensureInBusinessHours(working.getStartTime(), working.getEndTime());

        if (working.getDoctorId() == null) {
            Long assignedDoctorId = autoAssignDoctor(service, working.getStartTime(), working.getEndTime());
            working = working.toBuilder()
                    .doctorId(assignedDoctorId)
                    .build();
        } else {
            lockDoctorProfile(working.getDoctorId());
        }

        validateAppointmentActors(working.getPatientId(), working.getDoctorId());

        Appointment prepared = working
                .prepareForCreate(generateAppointmentCode(working.getStartTime()))
                .ensureEndTimeDefault();

        prepared = validateAndNormalizeTimeRange(prepared);
        validateDoctorBySchedule(prepared.getDoctorId(), service, prepared.getStartTime(), prepared.getEndTime());
        validateScheduleConflicts(null, prepared);

        Appointment saved;
        try {
            saved = appointmentRepository.saveAndFlush(prepared);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Khung gio vua duoc dat boi nguoi khac, vui long chon khung gio khac");
        }

        User patient = userRepository.findById(saved.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        activityLogService.add("APPOINTMENT", "Benh nhan " + patient.getFullName() + " vua dat lich thanh cong");

        return saved;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointments(
            Long doctorId,
            Long patientId,
            Boolean pending,
            Boolean today,
            Boolean doneThisMonth,
            String doctorName,
            String patientName,
            LocalDate appointmentDate,
            String status) {

        if (Boolean.TRUE.equals(doneThisMonth)) {
            return doctorId != null
                    ? getMonthDoneAppointmentsByDoctorId(doctorId)
                    : getMonthDoneAppointments();
        }

        if (Boolean.TRUE.equals(today)) {
            return Boolean.TRUE.equals(pending)
                    ? getTodayPendingAppointments()
                    : getTodayAppointments();
        }

        if (doctorId != null) {
            return Boolean.TRUE.equals(pending)
                    ? getPendingByDoctorId(doctorId)
                    : getByDoctorId(doctorId);
        }

        if (patientId != null) {
            return Boolean.TRUE.equals(pending)
                    ? getPendingByPatientId(patientId)
                    : getNotPendingByPatientId(patientId);
        }

        if (hasFilterCriteria(doctorName, patientName, appointmentDate, status)) {
            return filterAppointments(doctorName, patientName, appointmentDate, status);
        }

        return getAllAppointments();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay lich hen voi id=" + id));
    }

    public List<Appointment> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> result = appointmentRepository.findByStartTimeBetween(start, end);
        result.sort(Comparator.comparing(Appointment::getCreatedAt));
        return result;
    }

    public List<Appointment> getTodayPendingAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> result = appointmentRepository.findByStartTimeBetweenAndStatus(start, end, AppointmentStatus.PENDING);
        result.sort(Comparator.comparing(Appointment::getCreatedAt));
        return result;
    }

    public List<Appointment> getMonthDoneAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        return appointmentRepository.findByStartTimeBetweenAndStatus(start, end, AppointmentStatus.DONE);
    }

    public List<Appointment> getMonthDoneAppointmentsByDoctorId(Long doctorId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        return appointmentRepository.findByStartTimeBetweenAndStatusAndDoctorId(start, end, AppointmentStatus.DONE, doctorId);
    }

    public List<Appointment> getPendingByPatientId(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByStatusAndPatientId(AppointmentStatus.PENDING, patientId);
        appointments.sort(Comparator.comparing(Appointment::getStartTime));
        return appointments;
    }

    public List<Appointment> getNotPendingByPatientId(Long patientId) {
        List<Appointment> done = appointmentRepository.findByStatusAndPatientId(AppointmentStatus.DONE, patientId);
        List<Appointment> cancelled = appointmentRepository.findByStatusAndPatientId(AppointmentStatus.CANCELLED, patientId);
        done.addAll(cancelled);
        done.sort(Comparator.comparing(Appointment::getStartTime));
        return done;
    }

    public List<Appointment> getPendingByDoctorId(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByStatusAndDoctorId(AppointmentStatus.PENDING, doctorId);
        appointments.sort(Comparator.comparing(Appointment::getStartTime));
        return appointments;
    }

    public List<Appointment> getByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public ApiResponse<Object> deleteById(Long id) {
        Appointment appointment = getAppointmentById(id);
        User patient = userRepository.findById(appointment.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        appointmentRepository.deleteById(id);
        activityLogService.add("APPOINTMENT", "Benh nhan " + patient.getFullName() + " da huy lich hen " + id);
        return ApiResponse.ok("DELETE SUCCESSFULLY", null);
    }

    public ApiResponse<Object> updateStatusDone(Long id) {
        Appointment appointment = getAppointmentById(id).withStatus(AppointmentStatus.DONE);
        appointmentRepository.save(appointment);
        return ApiResponse.ok("UPDATE APPOINTMENT STATUS SUCCESSFULLY", null);
    }

    public ApiResponse<Object> updateStatusCancelled(Long id) {
        Appointment appointment = getAppointmentById(id).withStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return ApiResponse.ok("UPDATE APPOINTMENT STATUS SUCCESSFULLY", null);
    }

    public ApiResponse<Object> updateStatus(Long id, String status) {
        AppointmentStatus nextStatus = parseStatus(status);
        if (nextStatus != AppointmentStatus.DONE && nextStatus != AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Chi ho tro cap nhat trang thai DONE hoac CANCELLED");
        }

        Appointment appointment = getAppointmentById(id).withStatus(nextStatus);
        appointmentRepository.save(appointment);
        return ApiResponse.ok("UPDATE APPOINTMENT STATUS SUCCESSFULLY", null);
    }

    @Transactional
    public Appointment updateAppointment(Long id, Appointment update) {
        Appointment appointment = getAppointmentById(id)
                .mergeFrom(update)
                .ensureEndTimeDefault();

        validateAppointmentActors(appointment.getPatientId(), appointment.getDoctorId());

        Long serviceId = appointment.getServiceId();
        if (serviceId == null) {
            serviceId = extractServiceIdFromLegacyNote(appointment.getNote());
        }

        com.acare.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        if (Boolean.FALSE.equals(service.getActive())) {
            throw new BadRequestException("Dich vu dang tam ngung hoat dong");
        }

        if (service.getDurationMin() == null || service.getDurationMin() <= 0) {
            throw new BadRequestException("Dich vu chua duoc cau hinh thoi gian kham hop le");
        }

        appointment = appointment.toBuilder()
                .serviceId(serviceId)
                .endTime(appointment.getStartTime().plusMinutes(service.getDurationMin()))
                .build();

        appointment = validateAndNormalizeTimeRange(appointment);
        ensureInBusinessHours(appointment.getStartTime(), appointment.getEndTime());
        lockDoctorProfile(appointment.getDoctorId());
        validateDoctorBySchedule(appointment.getDoctorId(), service, appointment.getStartTime(), appointment.getEndTime());
        validateScheduleConflicts(id, appointment);

        try {
            return appointmentRepository.saveAndFlush(appointment);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Khung gio vua duoc dat boi nguoi khac, vui long chon khung gio khac");
        }
    }

    public List<Appointment> filterAppointments(
            String doctorName,
            String patientName,
            LocalDate appointmentDate,
            String status) {
        List<Appointment> appointments = appointmentRepository.findAll();

        if (doctorName != null && !doctorName.trim().isEmpty()) {
            List<Long> doctorIds = userRepository.findByRole(UserRole.DOCTOR).stream()
                    .filter(doctor -> doctor.getFullName() != null
                            && doctor.getFullName().toLowerCase().contains(doctorName.toLowerCase()))
                    .map(User::getId)
                    .collect(Collectors.toList());

            appointments = appointments.stream()
                    .filter(apt -> doctorIds.contains(apt.getDoctorId()))
                    .collect(Collectors.toList());
        }

        if (patientName != null && !patientName.trim().isEmpty()) {
            List<Long> patientIds = userRepository.findByRole(UserRole.PATIENT).stream()
                    .filter(patient -> patient.getFullName() != null
                            && patient.getFullName().toLowerCase().contains(patientName.toLowerCase()))
                    .map(User::getId)
                    .collect(Collectors.toList());

            appointments = appointments.stream()
                    .filter(apt -> patientIds.contains(apt.getPatientId()))
                    .collect(Collectors.toList());
        }

        if (appointmentDate != null) {
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();
            LocalDateTime endOfDay = appointmentDate.atTime(23, 59, 59);

            appointments = appointments.stream()
                    .filter(apt -> apt.getStartTime() != null
                            && !apt.getStartTime().isBefore(startOfDay)
                            && !apt.getStartTime().isAfter(endOfDay))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty()) {
            AppointmentStatus statusFilter = parseStatus(status);
            appointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == statusFilter)
                    .collect(Collectors.toList());
        }

        appointments.sort(Comparator.comparing(Appointment::getStartTime));
        return appointments;
    }

    public List<java.util.Map<String, Object>> getDoctorsByServiceId(Long serviceId) {
        com.acare.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        List<DoctorProfile> profiles = getDoctorProfilesForService(service);
        List<java.util.Map<String, Object>> result = new ArrayList<>();

        for (DoctorProfile profile : profiles) {
            User user = userRepository.findById(profile.getUserId()).orElse(null);
            if (user == null) {
                continue;
            }

            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", user.getId());
            map.put("fullName", user.getFullName());
            map.put("phone", user.getPhone());
            map.put("specialtyId", profile.getSpecialtyId());
            map.put("biography", profile.getBiography());
            map.put("workingDays", profile.getWorkingDays());
            map.put("clinicLocation", profile.getClinicLocation());
            result.add(map);
        }

        return result;
    }

    public List<AppointmentAvailabilityOption> getDoctorAvailability(Long doctorId, Long serviceId, LocalDate date) {
        if (date == null) {
            throw new BadRequestException("Ngay dat lich khong duoc de trong");
        }

        com.acare.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        int durationMinutes = resolveDurationMinutes(service);
        DoctorProfile profile = doctorProfileRepository.findByUserId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bac si"));

        List<AppointmentAvailabilityOption> options = new ArrayList<>();
        LocalTime cursor = BUSINESS_START;
        LocalTime lastStart = BUSINESS_END.minusMinutes(durationMinutes);

        while (!cursor.isAfter(lastStart)) {
            LocalDateTime start = date.atTime(cursor);
            LocalDateTime end = start.plusMinutes(durationMinutes);

            boolean available = isDoctorAvailable(profile, service, start, end);
            options.add(AppointmentAvailabilityOption.builder()
                    .time(cursor.toString())
                    .availableDoctors(available ? 1 : 0)
                    .available(available)
                    .build());

            cursor = cursor.plusMinutes(30);
        }

        return options;
    }

    public List<AppointmentAvailabilityOption> getAvailability(Long serviceId, LocalDate date) {
        if (date == null) {
            throw new BadRequestException("Ngay dat lich khong duoc de trong");
        }

        com.acare.backend.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        int durationMinutes = resolveDurationMinutes(service);
        List<DoctorProfile> doctorProfiles = getDoctorProfilesForService(service);

        List<AppointmentAvailabilityOption> options = new ArrayList<>();
        LocalTime cursor = BUSINESS_START;
        LocalTime lastStart = BUSINESS_END.minusMinutes(durationMinutes);

        while (!cursor.isAfter(lastStart)) {
            LocalDateTime start = date.atTime(cursor);
            LocalDateTime end = start.plusMinutes(durationMinutes);

            int doctorCount = (int) doctorProfiles.stream()
                    .filter(profile -> isDoctorAvailable(profile, service, start, end))
                    .count();

            options.add(AppointmentAvailabilityOption.builder()
                    .time(cursor.toString())
                    .availableDoctors(doctorCount)
                    .available(doctorCount > 0)
                    .build());

            cursor = cursor.plusMinutes(30);
        }

        return options;
    }

    private int resolveDurationMinutes(com.acare.backend.entity.Service service) {
        Integer duration = service.getDurationMin();
        int durationMinutes = duration != null ? duration : 30;
        if (durationMinutes <= 0) {
            throw new BadRequestException("Dich vu co thoi luong khong hop le");
        }
        return durationMinutes;
    }

    private AppointmentStatus parseStatus(String status) {
        try {
            return AppointmentStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Trang thai lich hen khong hop le");
        }
    }

    private void validateAppointmentActors(Long patientId, Long doctorId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        if (patient.getRole() != UserRole.PATIENT) {
            throw new BadRequestException("Nguoi dat lich phai co role PATIENT");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bac si"));
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new BadRequestException("Nguoi duoc dat lich phai co role DOCTOR");
        }
    }

    private void validatePatientOnly(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        if (patient.getRole() != UserRole.PATIENT) {
            throw new BadRequestException("Nguoi dat lich phai co role PATIENT");
        }
    }

    private String normalizeCode(String rawValue, String defaultValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultValue;
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    private boolean hasFilterCriteria(
            String doctorName,
            String patientName,
            LocalDate appointmentDate,
            String status) {
        return (doctorName != null && !doctorName.isBlank())
                || (patientName != null && !patientName.isBlank())
                || appointmentDate != null
                || (status != null && !status.isBlank());
    }

    private Appointment validateAndNormalizeTimeRange(Appointment appointment) {
        if (appointment.getStartTime() == null) {
            throw new BadRequestException("Start time khong duoc de trong");
        }

        LocalDateTime endTime = appointment.getEndTime() != null
                ? appointment.getEndTime()
                : appointment.getStartTime().plusMinutes(30);

        if (!endTime.isAfter(appointment.getStartTime())) {
            throw new BadRequestException("End time phai lon hon start time");
        }

        return appointment.toBuilder().endTime(endTime).build();
    }

    private void validateScheduleConflicts(Long appointmentId, Appointment appointment) {
        boolean doctorConflict = appointmentId == null
                ? appointmentRepository.existsDoctorConflict(
                        appointment.getDoctorId(),
                        appointment.getStartTime(),
                        appointment.getEndTime(),
                        ACTIVE_SCHEDULE_STATUSES)
                : appointmentRepository.existsDoctorConflictExcludingId(
                        appointmentId,
                        appointment.getDoctorId(),
                        appointment.getStartTime(),
                        appointment.getEndTime(),
                        ACTIVE_SCHEDULE_STATUSES);

        if (doctorConflict) {
            throw new ConflictException("Bac si da co lich hen trung khung gio");
        }
    }

    private String generateAppointmentCode(LocalDateTime startTime) {
        return "APT"
                + startTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void ensureInBusinessHours(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("Thoi gian dat lich khong hop le");
        }

        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new BadRequestException("Chi ho tro dat lich vao ngay lam viec");
        }

        LocalTime st = start.toLocalTime();
        LocalTime et = end.toLocalTime();
        if (st.isBefore(BUSINESS_START) || et.isAfter(BUSINESS_END)) {
            throw new BadRequestException("Chi ho tro dat lich trong gio hanh chinh 08:00-17:00");
        }
    }

    private Long extractServiceIdFromLegacyNote(String note) {
        if (note == null || note.isBlank()) {
            throw new BadRequestException("Vui long chon dich vu");
        }
        try {
            return Long.valueOf(note.trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Dich vu khong hop le");
        }
    }

    private List<DoctorProfile> getDoctorProfilesForService(com.acare.backend.entity.Service service) {
        ensureDoctorProfilesForExistingDoctors();

        if (service.getSpecialtyId() == null) {
            return List.of();
        }

        return doctorProfileRepository.findBySpecialtyIdAndOnLeaveFalse(service.getSpecialtyId());
    }

    private void ensureDoctorProfilesForExistingDoctors() {
        List<User> doctors = userRepository.findByRole(UserRole.DOCTOR);
        if (doctors.isEmpty()) {
            return;
        }

        DoctorSpecialization generalSpecialization = resolveGeneralDoctorSpecialization();

        List<DoctorProfile> missingProfiles = doctors.stream()
                .filter(doctor -> !doctorProfileRepository.existsByUserId(doctor.getId()))
                .map(doctor -> DoctorProfile.createForUser(doctor.getId(), generalSpecialization.specialtyId()).toBuilder()
                        .department(generalSpecialization.department())
                        .specialty(generalSpecialization.specialty())
                        .build())
                .collect(Collectors.toList());

        if (!missingProfiles.isEmpty()) {
            doctorProfileRepository.saveAll(missingProfiles);
        }
    }

    private Long autoAssignDoctor(
            com.acare.backend.entity.Service service,
            LocalDateTime start,
            LocalDateTime end) {
        List<DoctorProfile> doctors = getDoctorProfilesForService(service);
        if (doctors.isEmpty()) {
            throw new BadRequestException("Khong co bac si nao thuoc khoa nay");
        }

        for (DoctorProfile doctorProfile : doctors) {
            DoctorProfile lockedDoctor = lockDoctorProfile(doctorProfile.getUserId());
            if (isDoctorAvailable(lockedDoctor, service, start, end)) {
                return lockedDoctor.getUserId();
            }
        }

        throw new ConflictException("Khong con bac si trong trong khung gio ban chon");
    }

    private boolean isDoctorAvailable(
            DoctorProfile profile,
            com.acare.backend.entity.Service service,
            LocalDateTime start,
            LocalDateTime end) {
        if (Boolean.TRUE.equals(profile.getOnLeave())) {
            return false;
        }

        ensureInBusinessHours(start, end);

        if (!profile.worksOn(start.getDayOfWeek())) {
            return false;
        }

        if (!profile.isWorkingTime(start.toLocalTime(), end.toLocalTime())) {
            return false;
        }

        if (service.getSpecialtyId() == null || profile.getSpecialtyId() == null) {
            return false;
        }

        if (!service.getSpecialtyId().equals(profile.getSpecialtyId())) {
            return false;
        }

        return !appointmentRepository.existsDoctorConflict(
                profile.getUserId(),
                start,
                end,
                ACTIVE_SCHEDULE_STATUSES);
    }

    private void validateDoctorBySchedule(
            Long doctorId,
            com.acare.backend.entity.Service service,
            LocalDateTime start,
            LocalDateTime end) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(doctorId)
                .orElseThrow(() -> new BadRequestException("Bac si chua duoc cau hinh ho so lam viec"));

        if (!isDoctorAvailable(profile, service, start, end)) {
            throw new ConflictException("Bac si khong ranh trong khung gio dat lich");
        }
    }

    private DoctorProfile lockDoctorProfile(Long doctorId) {
        DoctorSpecialization defaultSpecialization = resolveGeneralDoctorSpecialization();

        return doctorProfileRepository.findByUserIdForUpdate(doctorId)
                .orElseGet(() -> {
                    DoctorProfile profile = DoctorProfile.createForUser(doctorId, defaultSpecialization.specialtyId()).toBuilder()
                            .department(defaultSpecialization.department())
                            .specialty(defaultSpecialization.specialty())
                            .build();
                    doctorProfileRepository.save(profile);
                    return doctorProfileRepository.findByUserIdForUpdate(doctorId)
                            .orElseThrow(() -> new BadRequestException("Bac si chua duoc cau hinh ho so lam viec"));
                });
    }

    private DoctorSpecialization resolveGeneralDoctorSpecialization() {
        Specialty generalSpecialty = specialtyService.getRequiredByCode("GENERAL");
        return new DoctorSpecialization(
                generalSpecialty.getId(),
                normalizeCode(generalSpecialty.getCode(), "GENERAL"),
                generalSpecialty.getName());
    }

    private record DoctorSpecialization(Long specialtyId, String department, String specialty) {
    }
}
