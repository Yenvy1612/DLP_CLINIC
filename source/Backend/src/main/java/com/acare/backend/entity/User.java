package com.acare.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.acare.backend.dto.auth.UserProfileResponse;
import com.acare.backend.entity.enums.Gender;
import com.acare.backend.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;
    @Column(unique = true, length = 150)
    private String email;
    @Column(unique = true, length = 20)
    private String phone;
    @JsonIgnore
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private UserRole role = UserRole.PATIENT;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Gender gender = Gender.OTHER;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private String address;
    @Column(unique = true, length = 30)
    private String idNumber;
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User withDefaults() {
        return this.toBuilder()
                .role(this.role == null ? UserRole.PATIENT : this.role)
                .gender(this.gender == null ? Gender.OTHER : this.gender)
                .enabled(this.enabled == null ? Boolean.TRUE : this.enabled)
                .build();
    }

    public User withEncodedPassword(String encodedPassword) {
        return this.toBuilder()
                .passwordHash(encodedPassword)
                .build();
    }

    public User mergeFrom(User update) {
        if (update == null) {
            return this;
        }

        return this.toBuilder()
                .fullName(update.getFullName() != null ? update.getFullName() : this.fullName)
                .email(update.getEmail() != null ? update.getEmail() : this.email)
                .phone(update.getPhone() != null ? update.getPhone() : this.phone)
                .gender(update.getGender() != null ? update.getGender() : this.gender)
                .birthDate(update.getBirthDate() != null ? update.getBirthDate() : this.birthDate)
                .address(update.getAddress() != null ? update.getAddress() : this.address)
                .idNumber(update.getIdNumber() != null ? update.getIdNumber() : this.idNumber)
                .role(update.getRole() != null ? update.getRole() : this.role)
                .enabled(update.getEnabled() != null ? update.getEnabled() : this.enabled)
                .build();
    }

    public UserProfileResponse toProfileResponse() {
        return UserProfileResponse.builder()
                .id(this.id)
                .fullName(this.fullName)
                .email(this.email)
                .phone(this.phone)
                .role(this.role != null ? this.role.name() : null)
                .gender(this.gender != null ? this.gender.name() : null)
                .birthDate(this.birthDate)
                .address(this.address)
                .idNumber(this.idNumber)
                .enabled(this.enabled)
                .build();
    }

    public static User createAdmin(String fullName, String email, String phone, String idNumber,
            String address, String encodedPassword) {
        return User.builder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .idNumber(idNumber)
                .address(address)
                .role(UserRole.ADMIN)
                .gender(Gender.OTHER)
                .enabled(true)
                .passwordHash(encodedPassword)
                .build();
    }
}
