package com.acare.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.Invoice;
import com.acare.backend.entity.InvoiceItem;
import com.acare.backend.entity.MedicalRecord;
import com.acare.backend.entity.MedicalRecordServiceItem;
import com.acare.backend.entity.PaymentTransaction;
import com.acare.backend.entity.enums.InvoiceStatus;
import com.acare.backend.entity.enums.PaymentStatus;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.InvoiceItemRepository;
import com.acare.backend.repository.InvoiceRepository;
import com.acare.backend.repository.MedicalRecordRepository;
import com.acare.backend.repository.MedicalRecordServiceRepository;
import com.acare.backend.repository.PaymentTransactionRepository;
import com.acare.backend.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordServiceRepository medicalRecordServiceRepository;
    private final ServiceRepository serviceRepository;
    private final ActivityLogService activityLogService;

    public Invoice createInvoice(Invoice request) {
        if (request.getMedicalRecordId() == null) {
            throw new BadRequestException("Medical record id khong duoc de trong");
        }

        if (invoiceRepository.findByMedicalRecordId(request.getMedicalRecordId()).isPresent()) {
            throw new BadRequestException("Ho so benh an nay da co hoa don");
        }

        MedicalRecord record = medicalRecordRepository.findById(request.getMedicalRecordId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so benh an"));

        Invoice prepared = request.prepareForCreate(record.getPatientId(), generateInvoiceNumber());
        Invoice saved = invoiceRepository.save(prepared);
        seedInvoiceItemsFromMedicalRecord(saved.getId(), record.getId());
        Invoice recalculated = recalculateInvoiceTotals(saved.getId());

        activityLogService.add("BILLING", "Tao hoa don " + recalculated.getInvoiceNumber());
        return recalculated;
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoa don"));
    }

    public List<Invoice> getInvoicesByPatientId(Long patientId) {
        return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<InvoiceItem> getInvoiceItems(Long invoiceId) {
        getInvoiceById(invoiceId);
        return invoiceItemRepository.findByInvoiceId(invoiceId);
    }

    public List<PaymentTransaction> getPaymentsByInvoiceId(Long invoiceId) {
        getInvoiceById(invoiceId);
        return paymentTransactionRepository.findByInvoiceIdOrderByCreatedAtDesc(invoiceId);
    }

    public InvoiceItem addInvoiceItem(Long invoiceId, InvoiceItem item) {
        getInvoiceById(invoiceId);

        InvoiceItem prepared = item.prepareForInvoice(invoiceId);

        if (prepared.getDescription() == null || prepared.getDescription().isBlank()) {
            throw new BadRequestException("Mo ta dong hoa don khong duoc de trong");
        }

        InvoiceItem saved = invoiceItemRepository.save(prepared);
        recalculateInvoiceTotals(invoiceId);
        return saved;
    }

    public Invoice issueInvoice(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId).markIssued(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public PaymentTransaction recordPayment(Long invoiceId, PaymentTransaction payment) {
        Invoice invoice = getInvoiceById(invoiceId);
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BadRequestException("Khong the thanh toan hoa don da huy");
        }

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("So tien thanh toan khong hop le");
        }

        PaymentTransaction prepared = payment.prepareForRecord(invoiceId, generatePaymentReference(), LocalDateTime.now());
        PaymentTransaction saved = paymentTransactionRepository.save(prepared);

        if (saved.getStatus() == PaymentStatus.SUCCESS) {
            LocalDateTime paidAt = saved.getPaidAt() != null ? saved.getPaidAt() : LocalDateTime.now();
            invoiceRepository.save(invoice.markPaid(paidAt));
        }

        activityLogService.add("PAYMENT", "Ghi nhan thanh toan cho hoa don " + invoice.getInvoiceNumber());
        return saved;
    }

    private void seedInvoiceItemsFromMedicalRecord(Long invoiceId, Long medicalRecordId) {
        List<MedicalRecordServiceItem> recordServices = medicalRecordServiceRepository.findByMedicalRecordId(medicalRecordId);
        for (MedicalRecordServiceItem rs : recordServices) {
            String description = serviceRepository.findById(rs.getServiceId())
                    .map(com.acare.backend.entity.Service::getName)
                    .orElse("Service #" + rs.getServiceId());
            InvoiceItem item = InvoiceItem.fromMedicalRecordService(invoiceId, rs, description);
            invoiceItemRepository.save(item);
        }
    }

    private Invoice recalculateInvoiceTotals(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoiceId);

        BigDecimal subtotal = items.stream()
                .map(InvoiceItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice recalculated = invoice.recalculateTotals(subtotal);
        return invoiceRepository.save(recalculated);
    }

    private String generateInvoiceNumber() {
        return "INV"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private String generatePaymentReference() {
        return "PAY"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
