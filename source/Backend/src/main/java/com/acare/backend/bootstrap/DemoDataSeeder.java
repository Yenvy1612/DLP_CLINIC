package com.acare.backend.bootstrap;

import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.Agent;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.entity.DoctorProfile;
import com.acare.backend.entity.MedicalRecord;
import com.acare.backend.entity.PatientProfile;
import com.acare.backend.entity.SecurityEvent;
import com.acare.backend.entity.Specialty;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.AgentStatus;
import com.acare.backend.entity.enums.AppointmentStatus;
import com.acare.backend.entity.enums.Gender;
import com.acare.backend.entity.enums.RiskLevel;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.repository.AgentRepository;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.DlpLogRepository;
import com.acare.backend.repository.DoctorProfileRepository;
import com.acare.backend.repository.MedicalRecordRepository;
import com.acare.backend.repository.PatientProfileRepository;
import com.acare.backend.repository.SecurityEventRepository;
import com.acare.backend.repository.ServiceRepository;
import com.acare.backend.repository.SpecialtyRepository;
import com.acare.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ServiceRepository serviceRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AgentRepository agentRepository;
    private final DlpLogRepository dlpLogRepository;
    private final SecurityEventRepository securityEventRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner seedDemoDataRunner() {
        return args -> seedDemoData();
    }

    private void seedDemoData() {
        Specialty general = ensureGeneralSpecialty();
        com.acare.backend.entity.Service service = ensureGeneralService(general.getId());

        User doctor = ensureDoctorUser();
        User doctor2 = ensureDoctorUser2();
        ensureDoctorProfile(doctor.getId(), general);
        ensureDoctorProfile(doctor2.getId(), general);

        User patient = ensurePatientUser();
        User patient2 = ensurePatientUser2();
        User admin = ensureAdminUser();
        ensurePatientProfile(patient.getId());
        ensurePatientProfile(patient2.getId());

        ensureAgent(doctor, "ANDROID-DOCTOR-DEMO-001", "Doctor Pixel");
        ensureAgent(patient, "ANDROID-PATIENT-DEMO-001", "Patient Redmi");
        ensureAgent(admin, "ANDROID-ADMIN-DEMO-001", "Admin Samsung");

        Appointment doneApt = ensureDoneAppointment(patient.getId(), doctor.getId(), service.getId());
        ensureMedicalRecord(doneApt.getId(), patient.getId(), doctor.getId());
        ensurePendingAppointment(patient.getId(), doctor.getId(), service.getId());
        ensureAppointmentVariants(patient.getId(), doctor.getId(), service.getId());
        ensureAppointmentVariants(patient2.getId(), doctor2.getId(), service.getId());
        ensureMedicalRecordsForAllPatients(doctor.getId(), service.getId());

        ensureDlpLogs(patient, admin);
        ensureSecurityEvents(patient, admin);

        log.info("[SEED] Demo data ready. Admin={}, Doctor1={}, Doctor2={}, Patient1={}, Patient2={}, Service={}",
                admin.getEmail(), doctor.getEmail(), doctor2.getEmail(), patient.getEmail(), patient2.getEmail(), service.getName());
    }

    private Specialty ensureGeneralSpecialty() {
        return specialtyRepository.findByCodeIgnoreCase("GENERAL")
                .orElseGet(() -> specialtyRepository.save(
                        Specialty.builder()
                                .code("GENERAL")
                                .name("General Medicine")
                                .active(true)
                                .build()
                ));
    }

    private com.acare.backend.entity.Service ensureGeneralService(Long specialtyId) {
        Optional<com.acare.backend.entity.Service> existing = serviceRepository.findByName("Kham tong quat")
                .stream()
                .findFirst();
        if (existing.isPresent()) return existing.get();

        return serviceRepository.save(com.acare.backend.entity.Service.builder()
                .name("Kham tong quat")
                .price(new BigDecimal("200000"))
                .description("Goi kham tong quat co ban")
                .department("GENERAL")
                .specialtyId(specialtyId)
                .active(true)
                .build());
    }

    private User ensureDoctorUser() {
        return userRepository.findByEmailIgnoreCase("doctor.demo@acare.local")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Doctor Demo")
                                .email("doctor.demo@acare.local")
                                .phone("0900001001")
                                .passwordHash(passwordEncoder.encode("Doctor@123"))
                                .role(UserRole.DOCTOR)
                                .gender(Gender.OTHER)
                                .enabled(true)
                                .address("Clinic CS1")
                                .build()
                ));
    }

    private User ensurePatientUser() {
        return userRepository.findByEmailIgnoreCase("patient.demo@acare.local")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Patient Demo")
                                .email("patient.demo@acare.local")
                                .phone("0900002002")
                                .idNumber("079201234567")
                                .passwordHash(passwordEncoder.encode("Patient@123"))
                                .role(UserRole.PATIENT)
                                .gender(Gender.OTHER)
                                .enabled(true)
                                .address("HCM City")
                                .build()
                ));
    }

    private User ensureDoctorUser2() {
        return userRepository.findByEmailIgnoreCase("doctor2.demo@acare.local")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Doctor Demo 2")
                                .email("doctor2.demo@acare.local")
                                .phone("0900001002")
                                .passwordHash(passwordEncoder.encode("Doctor@123"))
                                .role(UserRole.DOCTOR)
                                .gender(Gender.OTHER)
                                .enabled(true)
                                .address("Clinic CS2")
                                .build()
                ));
    }

    private User ensurePatientUser2() {
        return userRepository.findByEmailIgnoreCase("patient2.demo@acare.local")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Patient Demo 2")
                                .email("patient2.demo@acare.local")
                                .phone("0900002003")
                                .idNumber("079201234568")
                                .passwordHash(passwordEncoder.encode("Patient@123"))
                                .role(UserRole.PATIENT)
                                .gender(Gender.FEMALE)
                                .enabled(true)
                                .address("Da Nang")
                                .build()
                ));
    }

    private User ensureAdminUser() {
        return userRepository.findByEmailIgnoreCase("admin@acare.local")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .fullName("Admin Demo")
                                .email("admin@acare.local")
                                .phone("0900003003")
                                .passwordHash(passwordEncoder.encode("Admin@123"))
                                .role(UserRole.ADMIN)
                                .gender(Gender.OTHER)
                                .enabled(true)
                                .address("Head Office")
                                .build()
                ));
    }

    private void ensurePatientProfile(Long userId) {
        if (patientProfileRepository.existsByUserId(userId)) return;
        patientProfileRepository.save(PatientProfile.builder()
                .userId(userId)
                .bloodType("O+")
                .insuranceNumber("BHYT-" + userId + "-2026")
                .allergies("Di ung hai san")
                .chronicConditions("Tang huyet ap")
                .emergencyContactName("Nguoi than " + userId)
                .emergencyContactPhone("0909000" + String.format("%03d", userId % 1000))
                .build());
    }

    private void ensureAgent(User user, String deviceId, String deviceName) {
        if (agentRepository.findByDeviceId(deviceId).isPresent()) return;
        agentRepository.save(Agent.builder()
                .deviceId(deviceId)
                .platform("ANDROID")
                .deviceName(deviceName)
                .hostname(null)
                .osVersion("Android 15")
                .agentVersion("1.0.0")
                .appVersion("1.0.0-debug")
                .ipAddress("192.168.1.99")
                .trusted(true)
                .status(AgentStatus.ONLINE)
                .registeredAt(LocalDateTime.now().minusHours(3))
                .lastHeartbeat(LocalDateTime.now().minusMinutes(1))
                .username(user.getEmail())
                .user(user)
                .build());
    }

    private void ensureDoctorProfile(Long doctorId, Specialty specialty) {
        if (doctorProfileRepository.existsByUserId(doctorId)) return;

        DoctorProfile profile = DoctorProfile.builder()
                .userId(doctorId)
                .specialtyId(specialty.getId())
                .specialty(specialty.getName())
                .department("GENERAL")
                .workingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
                .shiftStart(LocalTime.of(8, 0))
                .shiftEnd(LocalTime.of(17, 0))
                .onLeave(false)
                .clinicLocation("CS1 - Tang 1")
                .yearsExperience(5)
                .biography("Doctor demo for mobile testing")
                .build();
        doctorProfileRepository.save(profile);
    }

    private Appointment ensureDoneAppointment(Long patientId, Long doctorId, Long serviceId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .filter(a -> a.getDoctorId().equals(doctorId) && a.getServiceId().equals(serviceId))
                .findFirst()
                .orElseGet(() -> {
                    LocalDateTime start = LocalDate.now().minusDays(1).atTime(9, 0);
                    Appointment apt = Appointment.builder()
                            .appointmentCode("APTDEMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                            .patientId(patientId)
                            .doctorId(doctorId)
                            .serviceId(serviceId)
                            .startTime(start)
                            .endTime(start.plusMinutes(30))
                            .status(AppointmentStatus.DONE)
                            .reason("Kham dinh ky")
                            .note("Demo appointment")
                            .build();
                    return appointmentRepository.save(apt);
                });
    }

    private void ensureMedicalRecord(Long appointmentId, Long patientId, Long doctorId) {
        if (medicalRecordRepository.existsByAppointmentId(appointmentId)) return;

        MedicalRecord record = MedicalRecord.builder()
                .recordCode("MRDEMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .appointmentId(appointmentId)
                .patientId(patientId)
                .doctorId(doctorId)
                .chiefComplaint("Dau dau nhe")
                .diagnosis("Cam cum mua")
                .treatmentPlan("Nghi ngoi va bo sung nuoc")
                .clinicalNotes("Theo doi them 3 ngay")
                .followUpDate(LocalDate.now().plusDays(7))
                .build();
        medicalRecordRepository.save(record);
    }

    private void ensurePendingAppointment(Long patientId, Long doctorId, Long serviceId) {
        boolean existsPending = appointmentRepository.findByStatusAndDoctorId(AppointmentStatus.PENDING, doctorId)
                .stream()
                .anyMatch(a -> a.getPatientId().equals(patientId));
        if (existsPending) return;

        LocalDateTime start = LocalDate.now().plusDays(1).atTime(10, 0);
        Appointment apt = Appointment.builder()
                .appointmentCode("APTDEMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .patientId(patientId)
                .doctorId(doctorId)
                .serviceId(serviceId)
                .startTime(start)
                .endTime(start.plusMinutes(30))
                .status(AppointmentStatus.PENDING)
                .reason("Tai kham")
                .note("Pending demo appointment")
                .build();
        appointmentRepository.save(apt);
    }

    private void ensureAppointmentVariants(Long patientId, Long doctorId, Long serviceId) {
        List<AppointmentStatus> statuses = List.of(AppointmentStatus.PENDING, AppointmentStatus.DONE, AppointmentStatus.CANCELLED);
        for (int i = 0; i < statuses.size(); i++) {
            AppointmentStatus status = statuses.get(i);
            LocalDateTime start = LocalDate.now().minusDays(4 - i).atTime(8 + i, 0);
            boolean exists = appointmentRepository.findByPatientId(patientId).stream().anyMatch(a ->
                    a.getDoctorId().equals(doctorId)
                            && a.getServiceId().equals(serviceId)
                            && a.getStartTime() != null
                            && a.getStartTime().toLocalDate().equals(start.toLocalDate())
                            && a.getStatus() == status);
            if (exists) continue;

            Appointment apt = Appointment.builder()
                    .appointmentCode("APTDEMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .patientId(patientId)
                    .doctorId(doctorId)
                    .serviceId(serviceId)
                    .startTime(start)
                    .endTime(start.plusMinutes(30))
                    .status(status)
                    .reason(status == AppointmentStatus.CANCELLED ? "Ban dot xuat" : "Kham dinh ky")
                    .note("Seed variant " + status.name())
                    .build();
            Appointment saved = appointmentRepository.save(apt);
            if (status == AppointmentStatus.DONE) {
                ensureMedicalRecord(saved.getId(), patientId, doctorId);
            }
        }
    }

    private void ensureDlpLogs(User patient, User admin) {
        if (dlpLogRepository.count() > 0) return;

        dlpLogRepository.save(DlpLog.builder()
                .deviceId("ANDROID-PATIENT-DEMO-001")
                .sourceType("PHONE")
                .platform("ANDROID")
                .eventType("FORM_DLP_MATCHED")
                .userId(patient.getId())
                .username(patient.getEmail())
                .action("INPUT_SCAN")
                .endpoint("/api/appointments")
                .httpMethod("POST")
                .contentSnippet("[SENSITIVE_WORD:HIV]")
                .violationType("SENSITIVE_WORD")
                .details("Dien noi dung vi pham: tu khoa HIV trong ly do kham.")
                .riskLevel(RiskLevel.HIGH)
                .blocked(true)
                .clientIp("192.168.1.99")
                .userAgent("Android/15")
                .timestamp(LocalDateTime.now().minusMinutes(20))
                .build());

        dlpLogRepository.save(DlpLog.builder()
                .deviceId("ANDROID-PATIENT-DEMO-001")
                .sourceType("PHONE")
                .platform("ANDROID")
                .eventType("EXPORT_ALLOWED")
                .userId(patient.getId())
                .username(patient.getEmail())
                .action("EXPORT_PDF")
                .endpoint("/local/export/pdf")
                .httpMethod("LOCAL")
                .contentSnippet("CCCD: 0792******67 | Email: pa****@acare.local")
                .violationType("MASKED_PII_ALLOWED")
                .details("Export ho so da mask thong tin nhay cam.")
                .riskLevel(RiskLevel.LOW)
                .blocked(false)
                .clientIp("192.168.1.99")
                .userAgent("Android/15")
                .timestamp(LocalDateTime.now().minusMinutes(10))
                .build());

        dlpLogRepository.save(DlpLog.builder()
                .deviceId("ANDROID-ADMIN-DEMO-001")
                .sourceType("PHONE")
                .platform("ANDROID")
                .eventType("COPY_PATIENT_DATA")
                .userId(admin.getId())
                .username(admin.getEmail())
                .action("COPY")
                .endpoint("/api/medical-records/patient/" + patient.getId())
                .httpMethod("GET")
                .contentSnippet("[CCCD_DETECTED: 0792******67]")
                .violationType("CCCD_DETECTED")
                .details("Co gang copy du lieu nhay cam cua benh nhan.")
                .riskLevel(RiskLevel.CRITICAL)
                .blocked(true)
                .clientIp("192.168.1.100")
                .userAgent("Android/15")
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .build());
    }

    private void ensureSecurityEvents(User patient, User admin) {
        if (securityEventRepository.count() > 0) return;

        securityEventRepository.save(SecurityEvent.builder()
                .userId(patient.getId())
                .eventType("HTTP_REQUEST_SPIKE")
                .severity("HIGH")
                .ipAddress("192.168.1.99")
                .requestUri("/api/appointments")
                .httpMethod("GET")
                .description("User gui tan suat request bat thuong trong 1 phut.")
                .riskScore(86)
                .actionTaken("WARNED")
                .occurredAt(LocalDateTime.now().minusMinutes(15))
                .build());

        securityEventRepository.save(SecurityEvent.builder()
                .userId(patient.getId())
                .eventType("DLP_BLOCKED_EXPORT")
                .severity("CRITICAL")
                .ipAddress("192.168.1.99")
                .requestUri("/local/export/pdf")
                .httpMethod("LOCAL")
                .description("He thong chan export vi phat hien tu khoa nhay cam chua duoc mask.")
                .riskScore(95)
                .actionTaken("BLOCKED")
                .occurredAt(LocalDateTime.now().minusMinutes(8))
                .build());

        securityEventRepository.save(SecurityEvent.builder()
                .userId(admin.getId())
                .eventType("TOKEN_REVOKED")
                .severity("CRITICAL")
                .ipAddress("192.168.1.100")
                .requestUri("/api/auth/logout")
                .httpMethod("POST")
                .description("Tu dong revoke session sau hanh vi critical.")
                .riskScore(99)
                .actionTaken("TOKEN_REVOKED")
                .occurredAt(LocalDateTime.now().minusMinutes(3))
                .build());
    }

    private void ensureMedicalRecordsForAllPatients(Long defaultDoctorId, Long serviceId) {
        List<User> patients = userRepository.findByRole(UserRole.PATIENT);
        int idx = 0;
        for (User p : patients) {
            List<Appointment> doneList = appointmentRepository.findByStatusAndPatientId(AppointmentStatus.DONE, p.getId());
            Appointment done;
            if (doneList.isEmpty()) {
                LocalDateTime start = findFreeDoctorSlot(defaultDoctorId, idx);
                done = appointmentRepository.save(Appointment.builder()
                        .appointmentCode("APTDEMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                        .patientId(p.getId())
                        .doctorId(defaultDoctorId)
                        .serviceId(serviceId)
                        .startTime(start)
                        .endTime(start.plusMinutes(30))
                        .status(AppointmentStatus.DONE)
                        .reason("Kham tong quat seed")
                        .note("Auto seed done appointment")
                        .build());
            } else {
                done = doneList.get(0);
            }
            ensureMedicalRecord(done.getId(), p.getId(), done.getDoctorId());
            idx++;
        }
    }

    private LocalDateTime findFreeDoctorSlot(Long doctorId, int seedOffset) {
        LocalDateTime start = LocalDate.now().minusDays(10 + seedOffset).atTime(8 + (seedOffset % 8), 0);
        for (int i = 0; i < 120; i++) {
            LocalDateTime candidate = start.plusMinutes(30L * i);
            boolean occupied = appointmentRepository.findByDoctorId(doctorId).stream()
                    .anyMatch(a -> a.getStartTime() != null && a.getStartTime().equals(candidate));
            if (!occupied) {
                return candidate;
            }
        }
        return LocalDate.now().minusDays(30).atTime(7, 0).plusMinutes(seedOffset);
    }
}
