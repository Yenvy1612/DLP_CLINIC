package com.acare.backend.dlp;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.acare.backend.entity.MedicalRecord;
import com.acare.backend.entity.PatientProfile;
import com.acare.backend.entity.User;
import com.acare.backend.repository.MedicalRecordRepository;
import com.acare.backend.repository.PatientProfileRepository;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Runner chạy 1 lần khi khởi động ứng dụng để MÃ HÓA DỮ LIỆU CŨ.
 *
 * Vấn đề:
 *   Trước khi bật AES encryption, dữ liệu trong DB là plaintext.
 *   Sau khi bật @Convert(AttributeEncryptor), JPA sẽ:
 *   - Khi ĐỌC: cố giải mã plaintext → fail → AttributeEncryptor trả về raw value (backward compatible)
 *   - Khi GHI: mã hóa rồi lưu → OK
 *
 * Giải pháp:
 *   Runner này đọc tất cả entity → JPA tự decrypt (backward compatible cho plaintext)
 *   → save lại → JPA tự encrypt → dữ liệu cũ giờ đã được mã hóa.
 *
 * Chỉ cần chạy 1 lần. Sau đó có thể tắt bằng property: dlp.encryption.migrate-on-startup=false
 *
 * Lưu ý: Runner chạy SAU khi Flyway migration hoàn tất (Order = 100).
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(100) // Chạy sau các runner khác (AdminBootstrap chạy trước)
public class DataEncryptionRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    /** Flag bật/tắt migration. Mặc định = false (tắt). Bật lên khi cần migrate lần đầu. */
    @org.springframework.beans.factory.annotation.Value("${dlp.encryption.migrate-on-startup:false}")
    private boolean migrateOnStartup;

    @Override
    public void run(String... args) {
        // Chỉ chạy khi được bật
        if (!migrateOnStartup) {
            log.info("[DLP Encryption] Migration tắt (dlp.encryption.migrate-on-startup=false). Bỏ qua.");
            return;
        }

        log.info("[DLP Encryption] Bắt đầu mã hóa dữ liệu cũ...");

        try {
            // --- Mã hóa idNumber trong bảng users ---
            List<User> users = userRepository.findAll();
            int userCount = 0;
            for (User user : users) {
                if (user.getIdNumber() != null && !user.getIdNumber().isBlank()) {
                    // JPA sẽ tự encrypt khi save (nhờ @Convert)
                    // AttributeEncryptor.convertToEntityAttribute sẽ trả raw value nếu chưa encrypt
                    userRepository.save(user);
                    userCount++;
                }
            }
            log.info("[DLP Encryption] Đã mã hóa {} user records.", userCount);

            // --- Mã hóa fields nhạy cảm trong bảng patient_profiles ---
            List<PatientProfile> profiles = patientProfileRepository.findAll();
            int profileCount = 0;
            for (PatientProfile profile : profiles) {
                boolean needsSave = false;
                if (profile.getInsuranceNumber() != null) needsSave = true;
                if (profile.getAllergies() != null) needsSave = true;
                if (profile.getChronicConditions() != null) needsSave = true;

                if (needsSave) {
                    patientProfileRepository.save(profile);
                    profileCount++;
                }
            }
            log.info("[DLP Encryption] Đã mã hóa {} patient profile records.", profileCount);

            // --- Mã hóa fields nhạy cảm trong bảng medical_records ---
            List<MedicalRecord> records = medicalRecordRepository.findAll();
            int recordCount = 0;
            for (MedicalRecord record : records) {
                boolean needsSave = false;
                if (record.getDiagnosis() != null) needsSave = true;
                if (record.getTreatmentPlan() != null) needsSave = true;
                if (record.getClinicalNotes() != null) needsSave = true;

                if (needsSave) {
                    medicalRecordRepository.save(record);
                    recordCount++;
                }
            }
            log.info("[DLP Encryption] Đã mã hóa {} medical record records.", recordCount);

            log.info("[DLP Encryption] ✅ Migration hoàn tất! Hãy tắt dlp.encryption.migrate-on-startup=false.");
        } catch (Exception e) {
            log.error("[DLP Encryption] ❌ Lỗi migration: {}", e.getMessage(), e);
        }
    }
}
