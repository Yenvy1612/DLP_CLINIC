package com.acare.backend.dlp;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO chứa kết quả sau khi DlpScannerService quét 1 đoạn text.
 *
 * Ví dụ sử dụng:
 *   DlpScanResult result = dlpScannerService.scan(requestBody);
 *   if (result.isViolated()) {
 *       // Ghi log + chặn request
 *       log.warn("Vi phạm: {}", result.getViolations());
 *   }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DlpScanResult {

    /**
     * true = phát hiện vi phạm (có dữ liệu nhạy cảm)
     * false = an toàn, không vi phạm
     */
    @Builder.Default
    private boolean violated = false;

    /**
     * Danh sách các vi phạm cụ thể tìm thấy.
     * VD: ["CCCD_DETECTED: 012345678901", "SENSITIVE_WORD: HIV"]
     * Mỗi phần tử chứa loại vi phạm + giá trị match.
     */
    @Builder.Default
    private List<String> violations = new ArrayList<>();

    /**
     * Loại vi phạm nghiêm trọng nhất (dùng để ghi vào dlp_logs.violation_type).
     * Thứ tự ưu tiên: CCCD > PHONE > EMAIL > SENSITIVE_WORD
     * Nếu có nhiều loại, chỉ lưu cái nghiêm trọng nhất.
     */
    private String primaryViolationType;
}
