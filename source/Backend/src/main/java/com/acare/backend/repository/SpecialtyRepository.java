package com.acare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByActiveTrueOrderByNameAsc();
    Optional<Specialty> findByIdAndActiveTrue(Long id);
    Optional<Specialty> findByCodeIgnoreCase(String code);
}
