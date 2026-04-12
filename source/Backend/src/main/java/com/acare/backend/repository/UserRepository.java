package com.acare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByFullName(String fullName);
    List<User> findByRole(UserRole role);
    boolean existsByRole(UserRole role);
    Optional<User> findByIdNumber(String idNumber);
}