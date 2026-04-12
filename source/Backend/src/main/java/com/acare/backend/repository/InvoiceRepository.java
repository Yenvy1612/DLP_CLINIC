package com.acare.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByMedicalRecordId(Long medicalRecordId);
    List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
