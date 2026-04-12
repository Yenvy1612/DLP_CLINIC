package com.acare.backend.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(length = 120)
    private String specialty;

    @Column(name = "specialty_id", nullable = false)
    private Long specialtyId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", insertable = false, updatable = false)
    private Specialty specialtyMaster;

    @Column(length = 120, nullable = false)
    @Builder.Default
    private String department = "GENERAL";

    @Column(name = "working_days", nullable = false, length = 120)
    @Builder.Default
    private String workingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";

    @Column(name = "shift_start", nullable = false)
    @Builder.Default
    private LocalTime shiftStart = LocalTime.of(8, 0);

    @Column(name = "shift_end", nullable = false)
    @Builder.Default
    private LocalTime shiftEnd = LocalTime.of(17, 0);

    @Column(name = "on_leave", nullable = false)
    @Builder.Default
    private Boolean onLeave = false;

    @Column(name = "clinic_location", length = 200, nullable = false)
    @Builder.Default
    private String clinicLocation = "CS1 - Tầng 1";

    @Column(name = "license_number", length = 80, unique = true)
    private String licenseNumber;

    @Column(name = "years_experience", nullable = false)
    @Builder.Default
    private Integer yearsExperience = 0;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.yearsExperience == null) {
            this.yearsExperience = 0;
        }
        if (this.department == null || this.department.isBlank()) {
            this.department = "GENERAL";
        }
        if (this.workingDays == null || this.workingDays.isBlank()) {
            this.workingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";
        }
        if (this.shiftStart == null) {
            this.shiftStart = LocalTime.of(8, 0);
        }
        if (this.shiftEnd == null) {
            this.shiftEnd = LocalTime.of(17, 0);
        }
        if (this.onLeave == null) {
            this.onLeave = false;
        }
        if (this.clinicLocation == null || this.clinicLocation.isBlank()) {
            this.clinicLocation = "CS1 - Tầng 1";
        }
    }

    public boolean worksOn(DayOfWeek dayOfWeek) {
        if (dayOfWeek == null || workingDays == null || workingDays.isBlank()) {
            return false;
        }
        Set<String> daySet = Arrays.stream(workingDays.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return daySet.contains(dayOfWeek.name());
    }

    public boolean isWorkingTime(LocalTime start, LocalTime end) {
        if (start == null || end == null || shiftStart == null || shiftEnd == null) {
            return false;
        }
        return !start.isBefore(shiftStart) && !end.isAfter(shiftEnd);
    }

    public static DoctorProfile createForUser(Long userId, Long specialtyId) {
        return DoctorProfile.builder()
                .userId(userId)
                .specialtyId(specialtyId)
                .department("GENERAL")
                .yearsExperience(0)
                .workingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
                .shiftStart(LocalTime.of(8, 0))
                .shiftEnd(LocalTime.of(17, 0))
                .onLeave(false)
                .build();
    }
}
