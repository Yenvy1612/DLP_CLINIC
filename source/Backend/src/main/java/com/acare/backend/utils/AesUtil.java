package com.acare.backend.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Tiện ích mã hóa AES-256-GCM cho dữ liệu nhạy cảm.
 *
 * Thuật toán: AES/GCM/NoPadding (256-bit key)
 * - AES: Advanced Encryption Standard — chuẩn mã hóa đối xứng mạnh nhất hiện tại
 * - GCM: Galois/Counter Mode — vừa mã hóa, vừa xác thực (authenticated encryption)
 * - Mỗi lần encrypt đều tạo IV (Initialization Vector) ngẫu nhiên → cùng plaintext sẽ cho ciphertext khác nhau
 *
 * Format output: Base64( IV_12_bytes + CipherText + AuthTag_16_bytes )
 *
 * Sử dụng:
 *   String encrypted = AesUtil.encrypt("012345678901", secretKey);
 *   String decrypted = AesUtil.decrypt(encrypted, secretKey);
 *   // decrypted == "012345678901"
 */
public class AesUtil {

    /** Thuật toán mã hóa: AES với chế độ GCM, không padding */
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /** Độ dài IV (Initialization Vector): 12 bytes — tiêu chuẩn cho GCM */
    private static final int IV_LENGTH = 12;

    /** Độ dài Authentication Tag: 128 bits — đảm bảo tính toàn vẹn dữ liệu */
    private static final int TAG_LENGTH = 128;

    /** Bộ sinh số ngẫu nhiên an toàn (dùng cho IV) */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Mã hóa plaintext thành ciphertext Base64.
     *
     * Quy trình:
     * 1. Tạo IV ngẫu nhiên 12 bytes (đảm bảo mỗi lần encrypt cho kết quả khác nhau)
     * 2. Tạo AES cipher với key + IV
     * 3. Mã hóa plaintext
     * 4. Ghép IV + ciphertext → encode Base64
     *
     * @param plainText   dữ liệu gốc cần mã hóa
     * @param secretKeyB64 secret key dạng Base64 (32 bytes sau decode = 256 bits)
     * @return ciphertext dạng Base64 (chứa cả IV bên trong)
     */
    public static String encrypt(String plainText, String secretKeyB64) {
        if (plainText == null) return null;

        try {
            // Decode secret key từ Base64 → byte array
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyB64);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Tạo IV ngẫu nhiên 12 bytes
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            // Khởi tạo cipher ở chế độ ENCRYPT
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            // Mã hóa plaintext → ciphertext (đã bao gồm auth tag)
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Ghép IV + ciphertext vào 1 buffer, rồi encode Base64
            // Khi decrypt sẽ tách: 12 bytes đầu = IV, phần còn lại = ciphertext
            ByteBuffer buffer = ByteBuffer.allocate(IV_LENGTH + cipherText.length);
            buffer.put(iv);
            buffer.put(cipherText);

            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa AES: " + e.getMessage(), e);
        }
    }

    /**
     * Giải mã ciphertext Base64 về plaintext gốc.
     *
     * Quy trình:
     * 1. Decode Base64 → byte array
     * 2. Tách 12 bytes đầu = IV, phần còn lại = ciphertext
     * 3. Giải mã bằng AES-GCM với key + IV
     * 4. GCM tự động xác thực auth tag (nếu dữ liệu bị sửa → throw exception)
     *
     * @param encryptedBase64 ciphertext dạng Base64 (output của encrypt())
     * @param secretKeyB64    secret key dạng Base64 (phải giống key lúc encrypt)
     * @return plaintext gốc
     */
    public static String decrypt(String encryptedBase64, String secretKeyB64) {
        if (encryptedBase64 == null) return null;

        try {
            // Decode secret key
            byte[] keyBytes = Base64.getDecoder().decode(secretKeyB64);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Decode ciphertext từ Base64
            byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

            // Tách IV (12 bytes đầu) và ciphertext (phần còn lại)
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            // Khởi tạo cipher ở chế độ DECRYPT
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH, iv));

            // Giải mã → plaintext
            byte[] plainBytes = cipher.doFinal(cipherText);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi giải mã AES: " + e.getMessage(), e);
        }
    }
}
