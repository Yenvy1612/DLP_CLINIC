package com.acare.backend.dlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.acare.backend.common.RegexPattern;

import lombok.extern.slf4j.Slf4j;

/**
 * Service chịu trách nhiệm QUÉT và MASK dữ liệu nhạy cảm.
 *
 * Được tách riêng khỏi DlpFilter để:
 * 1. Dễ viết unit test (test scan logic mà không cần mock HTTP request)
 * 2. Có thể tái sử dụng ở nhiều nơi (Filter, Aspect, Controller)
 *
 * Hai chức năng chính:
 * - scan():  Quét text → trả về DlpScanResult (có vi phạm hay không)
 * - mask():  Thay thế dữ liệu nhạy cảm bằng dấu * (VD: 012345678901 → 0123****8901)
 */
@Service
@Slf4j
public class DlpScannerService {

    // ==================== QUÉT DỮ LIỆU NHẠY CẢM ====================

    /**
     * Quét nội dung text để tìm dữ liệu nhạy cảm.
     *
     * Quy trình:
     * 1. Duyệt qua tất cả regex pattern (CCCD, SĐT, Email)
     * 2. Kiểm tra từ khóa nhạy cảm (HIV, Ung thư, ...)
     * 3. Gom kết quả vào DlpScanResult
     *
     * @param content nội dung cần quét (request body hoặc response body)
     * @return kết quả scan chứa danh sách vi phạm
     */
    public DlpScanResult scan(String content) {
        // Nếu content rỗng → an toàn, không vi phạm
        if (content == null || content.isBlank()) {
            return DlpScanResult.builder().violated(false).build();
        }

        List<String> violations = new ArrayList<>();
        String primaryType = null;  // Loại vi phạm nghiêm trọng nhất

        // --- Bước 1: Quét các regex pattern (CCCD, SĐT, Email) ---
        for (Map.Entry<Pattern, String> entry : RegexPattern.PATTERN_NAMES.entrySet()) {
            Pattern pattern = entry.getKey();
            String violationName = entry.getValue();  // VD: "CCCD_DETECTED"

            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                String matchedValue = matcher.group();
                violations.add(violationName + ": " + matchedValue);

                // Ghi nhận loại vi phạm đầu tiên tìm thấy = nghiêm trọng nhất
                // (vì PATTERN_NAMES dùng LinkedHashMap, CCCD ở đầu)
                if (primaryType == null) {
                    primaryType = violationName;
                }
            }
        }

        // --- Bước 2: Quét từ khóa nhạy cảm ---
        // Dùng case-insensitive để bắt cả "hiv", "HIV", "Hiv"
        String contentLower = content.toLowerCase();
        for (String keyword : RegexPattern.SENSITIVE_WORDS) {
            if (contentLower.contains(keyword.toLowerCase())) {
                violations.add("SENSITIVE_WORD: " + keyword);

                if (primaryType == null) {
                    primaryType = "SENSITIVE_WORD";
                }
            }
        }

        // --- Bước 3: Tổng hợp kết quả ---
        boolean violated = !violations.isEmpty();

        if (violated) {
            log.warn("[DLP Scanner] Phát hiện {} vi phạm: {}", violations.size(), violations);
        }

        return DlpScanResult.builder()
                .violated(violated)
                .violations(violations)
                .primaryViolationType(primaryType)
                .build();
    }

    // ==================== MASK DỮ LIỆU NHẠY CẢM ====================

    /**
     * Thay thế dữ liệu nhạy cảm trong text bằng dấu *.
     *
     * Ví dụ:
     * - CCCD  "012345678901"  → "0123****8901"
     * - SĐT   "0987654321"   → "0987****21"
     * - Email "user@mail.com" → "us****@mail.com"
     *
     * Dùng cho OUTPUT CHECK: khi bác sĩ tải dữ liệu về,
     * response sẽ được mask trước khi trả về client.
     *
     * @param content nội dung cần mask
     * @return nội dung đã được mask, an toàn để trả về client
     */
    public String mask(String content) {
        if (content == null || content.isBlank()) {
            return content;
        }

        String masked = content;

        // Mask CCCD: giữ 4 ký tự đầu + 4 ký tự cuối, thay giữa bằng ****
        // VD: 012345678901 → 0123****8901
        masked = RegexPattern.CCCD_PATTERN.matcher(masked).replaceAll(match -> {
            String val = match.group();
            return val.substring(0, 4) + "****" + val.substring(val.length() - 4);
        });

        // Mask SĐT: giữ 4 ký tự đầu + 2 ký tự cuối, thay giữa bằng ****
        // VD: 0987654321 → 0987****21
        masked = RegexPattern.PHONE_PATTERN.matcher(masked).replaceAll(match -> {
            String val = match.group();
            return val.substring(0, 4) + "****" + val.substring(val.length() - 2);
        });

        // Mask Email: giữ 2 ký tự đầu username + domain, thay giữa bằng ****
        // VD: user@mail.com → us****@mail.com
        masked = RegexPattern.EMAIL_PATTERN.matcher(masked).replaceAll(match -> {
            String val = match.group();
            int atIndex = val.indexOf('@');
            if (atIndex > 2) {
                return val.substring(0, 2) + "****" + val.substring(atIndex);
            }
            return "****" + val.substring(atIndex); // Email ngắn thì mask hết username
        });

        return masked;
    }
}
