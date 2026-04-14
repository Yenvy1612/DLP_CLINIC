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

import com.acare.backend.dto.billing.InvoiceItemRequest;
import com.acare.backend.dto.billing.InvoiceItemResponse;
import com.acare.backend.dto.billing.InvoiceRequest;
import com.acare.backend.dto.billing.InvoiceResponse;
import com.acare.backend.dto.billing.PaymentTransactionRequest;
import com.acare.backend.dto.billing.PaymentTransactionResponse;
import com.acare.backend.service.BillingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(InvoiceResponse.from(billingService.createInvoice(request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices/{invoiceId}/items")
    public ResponseEntity<InvoiceItemResponse> addInvoiceItem(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceItemRequest request) {
        return ResponseEntity.ok(InvoiceItemResponse.from(billingService.addInvoiceItem(invoiceId, request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PatchMapping("/invoices/{invoiceId}/issue")
    public ResponseEntity<InvoiceResponse> issueInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(InvoiceResponse.from(billingService.issueInvoice(invoiceId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @PostMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<PaymentTransactionResponse> recordPayment(
            @PathVariable Long invoiceId,
            @RequestBody PaymentTransactionRequest request) {
        return ResponseEntity.ok(PaymentTransactionResponse.from(billingService.recordPayment(invoiceId, request.toEntity())));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(InvoiceResponse.from(billingService.getInvoiceById(invoiceId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(@RequestParam(required = false) Long patientId) {
        if (patientId != null) {
            return ResponseEntity.ok(billingService.getInvoicesByPatientId(patientId).stream().map(InvoiceResponse::from).toList());
        }
        return ResponseEntity.ok(List.of());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/patient/{patientId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByPatientId(@PathVariable Long patientId) {
        return getInvoices(patientId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}/items")
    public ResponseEntity<List<InvoiceItemResponse>> getInvoiceItems(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.getInvoiceItems(invoiceId).stream().map(InvoiceItemResponse::from).toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<List<PaymentTransactionResponse>> getPayments(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(billingService.getPaymentsByInvoiceId(invoiceId).stream().map(PaymentTransactionResponse::from).toList());
    }
}
