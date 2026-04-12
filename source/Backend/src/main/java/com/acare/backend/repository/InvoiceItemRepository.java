package com.acare.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
}
