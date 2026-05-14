package com.acare.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA AttributeConverter tự động mã hóa/giải mã dữ liệu khi đọc/ghi DB.
 *
 * Cách hoạt động:
 * - Khi JPA LƯU entity xuống DB → gọi convertToDatabaseColumn() → mã hóa AES
 * - Khi JPA ĐỌC entity từ DB    → gọi convertToEntityAttribute() → giải mã AES
 *
 * Sử dụng: đánh @Convert(converter = AttributeEncryptor.class) lên field entity.
 *
 * Ví dụ trong User.java:
 *   @Convert(converter = AttributeEncryptor.class)
 *   private String idNumber;  // CCCD sẽ được mã hóa khi lưu DB
 *
 * Lưu ý:
 * - Secret key được lấy từ application.properties (dlp.encryption.key)
 * - Secret key phải là Base64 string encode 32 bytes (256 bits)
 * - Converter xử lý null-safe: null input → null output
 * - Nếu giải mã thất bại (dữ liệu cũ chưa mã hóa) → trả về raw value (backward compatible)
 */
@Component
@Converter
@Slf4j
public class AttributeEncryptor implements AttributeConverter<String, String> {

    /**
     * Secret key cho AES encryption, đọc từ config.
     * Format: Base64 string, decode ra phải đúng 32 bytes.
     * Tạo bằng GenerateKey.java hoặc: openssl rand -base64 32
     */
    @Value("${dlp.encryption.key:}")
    private String secretKey;

    /**
     * Mã hóa trước khi lưu xuống database.
     * Input: plaintext (VD: "012345678901")
     * Output: Base64(IV + ciphertext) (VD: "a1b2c3d4...")
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Null hoặc encryption key rỗng → bỏ qua mã hóa
        if (attribute == null || secretKey == null || secretKey.isBlank()) {
            return attribute;
        }

        try {
            return AesUtil.encrypt(attribute, secretKey);
        } catch (Exception e) {
            log.error("[Encryptor] Lỗi mã hóa attribute: {}", e.getMessage());
            // Fallback: lưu plaintext nếu mã hóa lỗi (tránh mất dữ liệu)
            return attribute;
        }
    }

    /**
     * Giải mã khi đọc từ database.
     * Input: Base64(IV + ciphertext) từ DB
     * Output: plaintext (VD: "012345678901")
     *
     * Backward compatible: nếu dữ liệu cũ là plaintext (chưa encrypt),
     * decrypt sẽ fail → trả về raw value → vẫn đọc được dữ liệu cũ.
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        // Null hoặc encryption key rỗng → trả nguyên
        if (dbData == null || secretKey == null || secretKey.isBlank()) {
            return dbData;
        }

        try {
            return AesUtil.decrypt(dbData, secretKey);
        } catch (Exception e) {
            // Dữ liệu cũ (plaintext) sẽ không decrypt được → trả về nguyên
            // Điều này cho phép backward compatibility khi mới bật encryption
            log.debug("[Encryptor] Không thể giải mã (có thể là dữ liệu cũ chưa mã hóa): {}",
                    dbData.substring(0, Math.min(10, dbData.length())) + "...");
            return dbData;
        }
    }
}
