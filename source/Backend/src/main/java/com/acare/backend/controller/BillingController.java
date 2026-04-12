package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.entity.Invoice;
import com.acare.backend.entity.InvoiceItem;
import com.acare.backend.entity.PaymentTransaction;
import com.acare.backend.service.BillingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice request) {
        return ResponseEntity.ok(billingService.createInvoice(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices/{invoiceId}/items")
    public ResponseEntity<InvoiceItem> addInvoiceItem(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceItem item) {
        return ResponseEntity.ok(billingService.addInvoiceItem(invoiceId, item));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PatchMapping("/invoices/{invoiceId}/issue")
    public ResponseEntity<Invoice> issueInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.issueInvoice(invoiceId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<PaymentTransaction> recordPayment(
            @PathVariable Long invoiceId,
            @RequestBody PaymentTransaction payment) {
        return ResponseEntity.ok(billingService.recordPayment(invoiceId, payment));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.getInvoiceById(invoiceId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getInvoices(@RequestParam(required = false) Long patientId) {
        if (patientId != null) {
            return ResponseEntity.ok(billingService.getInvoicesByPatientId(patientId));
        }
        return ResponseEntity.ok(List.of());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/patient/{patientId}")
    public ResponseEntity<List<Invoice>> getInvoicesByPatientId(@PathVariable Long patientId) {
        return getInvoices(patientId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}/items")
    public ResponseEntity<List<InvoiceItem>> getInvoiceItems(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.getInvoiceItems(invoiceId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<List<PaymentTransaction>> getPayments(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.getPaymentsByInvoiceId(invoiceId));
    }
}
