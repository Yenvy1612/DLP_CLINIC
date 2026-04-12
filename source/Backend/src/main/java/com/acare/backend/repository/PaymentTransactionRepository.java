package com.acare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);
}
