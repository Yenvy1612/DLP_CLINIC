package com.acare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.MedicalRecordServiceItem;

public interface MedicalRecordServiceRepository extends JpaRepository<MedicalRecordServiceItem, Long> {
    List<MedicalRecordServiceItem> findByMedicalRecordId(Long medicalRecordId);
}
