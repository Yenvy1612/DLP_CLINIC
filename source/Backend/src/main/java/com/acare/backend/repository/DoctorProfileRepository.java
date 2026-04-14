package com.acare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acare.backend.entity.DoctorProfile;

import jakarta.persistence.LockModeType;

public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);
    List<DoctorProfile> findByUserIdIn(List<Long> userIds);
    boolean existsByUserId(Long userId);
    List<DoctorProfile> findBySpecialtyIdAndOnLeaveFalse(Long specialtyId);
    List<DoctorProfile> findByDepartmentIgnoreCaseAndOnLeaveFalse(String department);
    List<DoctorProfile> findByOnLeaveFalse();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DoctorProfile d where d.userId = :userId")
    Optional<DoctorProfile> findByUserIdForUpdate(@Param("userId") Long userId);
}
