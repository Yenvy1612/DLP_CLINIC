package com.acare.backend.service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.user.UserCreateRequest;
import com.acare.backend.entity.DoctorProfile;
import com.acare.backend.entity.PatientProfile;
import com.acare.backend.entity.Specialty;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.Gender;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.DoctorProfileRepository;
import com.acare.backend.repository.PatientProfileRepository;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpecialtyService specialtyService;
    private final ActivityLogService activityLogService;

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.sort(Comparator
                .comparing((User u) -> u.getRole().name())
                .thenComparing(User::getFullName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return users;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung voi id=" + id));
    }

    public List<User> getDoctors() {
        return userRepository.findByRole(UserRole.DOCTOR);
    }

    public List<User> getPatients() {
        return userRepository.findByRole(UserRole.PATIENT);
    }

    public ApiResponse<User> register(UserCreateRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD MISMATCH");
        }

        UserRole role = parseRoleOrDefault(request.getRole());
        DoctorSpecialization doctorSpecialization = resolveDoctorSpecialization(role, request);
        String doctorClinicLocation = resolveDoctorClinicLocation(role, request);

        User normalized = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(encodeIfPresent(request.getPassword()))
                .role(role)
                .gender(parseGenderOrDefault(request.getGender()))
                .birthDate(parseBirthDate(request.getBirthDate()))
                .address(request.getAddress())
                .idNumber(request.getIdNumber())
                .enabled(true)
                .build()
                .withDefaults();

        validateUniqueFields(normalized, null);

        User saved = userRepository.save(normalized);
        provisionProfileByRole(saved, doctorSpecialization, doctorClinicLocation, request);
        activityLogService.add("USER MANAGEMENT", "Tao tai khoan moi cho " + saved.getFullName());
        return ApiResponse.created("SIGN UP SUCCESSFULLY", saved);
    }

    public User updateUser(Long id, User update) {
        User user = getUserById(id).mergeFrom(update);

        user = normalizeDefaultValues(user);
        validateUniqueFields(user, id);

        User saved = userRepository.save(user);
        provisionProfileByRole(saved, null, null, null);

        return saved;
    }

    public ApiResponse<Object> deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.deleteById(id);
        activityLogService.add("USER MANAGEMENT", "Da xoa tai khoan nguoi dung " + user.getFullName());
        return ApiResponse.ok("DELETED SUCCESSFULLY", null);
    }

    public List<User> searchUsers(String fullName, String role, String email) {
        List<User> users = userRepository.findAll();

        // Search theo tên người dùng
        if (fullName != null && !fullName.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getFullName() != null && 
                            user.getFullName().toLowerCase().contains(fullName.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (role != null && !role.trim().isEmpty()) {
            UserRole roleFilter = parseRole(role);
            users = users.stream()
                .filter(user -> user.getRole() == roleFilter)
                    .collect(Collectors.toList());
        }

        if (email != null && !email.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> user.getEmail() != null && 
                            user.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return users;
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Role khong hop le");
        }
    }

    private User normalizeDefaultValues(User user) {
        return user.withDefaults();
    }

    private void validateUniqueFields(User user, Long currentUserId) {
        if (user.getEmail() != null) {
            userRepository.findByEmailIgnoreCase(user.getEmail())
                    .filter(found -> !found.getId().equals(currentUserId))
                    .ifPresent(found -> {
                        throw new BadRequestException("EMAIL WAS USED");
                    });
        }
        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            userRepository.findByPhone(user.getPhone())
                    .filter(found -> !found.getId().equals(currentUserId))
                    .ifPresent(found -> {
                        throw new BadRequestException("NUMBER WAS USED");
                    });
        }
        if (user.getIdNumber() != null && !user.getIdNumber().isBlank()) {
            userRepository.findByIdNumber(user.getIdNumber())
                    .filter(found -> !found.getId().equals(currentUserId))
                    .ifPresent(found -> {
                        throw new BadRequestException("ID NUMBER WAS USED");
                    });
        }
    }

    private void provisionProfileByRole(
            User user,
            DoctorSpecialization doctorSpecialization,
            String doctorClinicLocation,
            UserCreateRequest request) {
        if (user.getRole() == UserRole.DOCTOR && !doctorProfileRepository.existsByUserId(user.getId())) {
            DoctorSpecialization resolved = doctorSpecialization != null
                    ? doctorSpecialization
                    : resolveGeneralDoctorSpecialization();

            java.time.LocalTime start = java.time.LocalTime.of(8, 0);
            java.time.LocalTime end = java.time.LocalTime.of(17, 0);
            try {
                if (request != null && request.getShiftStart() != null && !request.getShiftStart().isBlank()) {
                    start = java.time.LocalTime.parse(request.getShiftStart(), DateTimeFormatter.ofPattern("HH:mm"));
                }
                if (request != null && request.getShiftEnd() != null && !request.getShiftEnd().isBlank()) {
                    end = java.time.LocalTime.parse(request.getShiftEnd(), DateTimeFormatter.ofPattern("HH:mm"));
                }
            } catch (Exception e) {}
            
            String days = (request != null && request.getWorkingDays() != null && !request.getWorkingDays().isBlank())
                 ? request.getWorkingDays() : "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";

            String clinicLocation = (doctorClinicLocation != null && !doctorClinicLocation.isBlank())
                    ? doctorClinicLocation.trim()
                    : "CS1 - Tầng 1";

            DoctorProfile profile = DoctorProfile.createForUser(user.getId(), resolved.specialtyId()).toBuilder()
                    .department(resolved.department())
                    .specialty(resolved.specialty())
                    .shiftStart(start)
                    .shiftEnd(end)
                    .workingDays(days)
                    .clinicLocation(clinicLocation)
                    .build();
            doctorProfileRepository.save(profile);
            return;
        }

        if (user.getRole() == UserRole.PATIENT && !patientProfileRepository.existsByUserId(user.getId())) {
            patientProfileRepository.save(PatientProfile.createForUser(user.getId()));
        }
    }

    private String encodeIfPresent(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException("Password khong duoc de trong");
        }

        if (rawPassword.startsWith("{")) {
            return rawPassword;
        }

        return passwordEncoder.encode(rawPassword);
    }

    private UserRole parseRoleOrDefault(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return UserRole.PATIENT;
        }

        try {
            return UserRole.valueOf(rawRole.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Role khong hop le");
        }
    }

    private Gender parseGenderOrDefault(String rawGender) {
        if (rawGender == null || rawGender.isBlank()) {
            return Gender.OTHER;
        }

        try {
            return Gender.valueOf(rawGender.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Gender khong hop le");
        }
    }

    private java.time.LocalDate parseBirthDate(String rawBirthDate) {
        if (rawBirthDate == null || rawBirthDate.isBlank()) {
            return null;
        }

        try {
            return java.time.LocalDate.parse(rawBirthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        try {
            return java.time.LocalDate.parse(rawBirthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Ngay sinh khong hop le");
        }
    }

    private DoctorSpecialization resolveDoctorSpecialization(UserRole role, UserCreateRequest request) {
        if (role != UserRole.DOCTOR) {
            return null;
        }

        if (request.getSpecialtyId() == null) {
            throw new BadRequestException("DOCTOR SPECIALTY IS REQUIRED");
        }

        Specialty specialty = specialtyService.getRequiredActiveById(request.getSpecialtyId());
        String resolvedDepartment = normalizeDepartmentValue(specialty.getCode());
        String resolvedSpecialty = specialty.getName();

        return new DoctorSpecialization(specialty.getId(), resolvedDepartment, resolvedSpecialty);
    }

    private String resolveDoctorClinicLocation(UserRole role, UserCreateRequest request) {
        if (role != UserRole.DOCTOR) {
            return null;
        }

        if (request.getClinicLocation() == null || request.getClinicLocation().isBlank()) {
            throw new BadRequestException("DOCTOR CLINIC LOCATION IS REQUIRED");
        }

        return request.getClinicLocation().trim();
    }

    private DoctorSpecialization resolveGeneralDoctorSpecialization() {
        Specialty generalSpecialty = specialtyService.getRequiredByCode("GENERAL");
        return new DoctorSpecialization(
                generalSpecialty.getId(),
                normalizeDepartmentValue(generalSpecialty.getCode()),
                generalSpecialty.getName());
    }

    private String normalizeDepartmentValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    private record DoctorSpecialization(Long specialtyId, String department, String specialty) {
    }
}
