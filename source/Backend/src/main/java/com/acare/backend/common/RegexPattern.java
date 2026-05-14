package com.acare.backend.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Tập hợp các Regex Pattern dùng để quét dữ liệu nhạy cảm.
 *
 * Được sử dụng bởi DlpScannerService để:
 * 1. Phát hiện thông tin cá nhân (PII): CCCD, SĐT, email
 * 2. Phát hiện từ khóa y tế nhạy cảm: HIV, Ung thư, ...
 *
 * Lưu ý: Dùng \b (word boundary) để tránh bắt nhầm chuỗi con.
 * VD: "0123456789012" sẽ match CCCD, nhưng "abc0123456789012xyz" cũng match
 *     vì \b bắt ranh giới giữa chữ và số.
 */
public class RegexPattern {

    // ==================== PATTERN NHẬN DIỆN THÔNG TIN CÁ NHÂN (PII) ====================

    /** CCCD/CMND: 12 chữ số bắt đầu bằng 0 (VD: 012345678901) */
    public static final Pattern CCCD_PATTERN = Pattern.compile("\\b0\\d{11}\\b");

    /** Số điện thoại Việt Nam: bắt đầu bằng 84 hoặc 03/05/07/08/09 + 8 chữ số */
    public static final Pattern PHONE_PATTERN = Pattern.compile("\\b(84|0[3|5|7|8|9])+([0-9]{8})\\b");

    /** Email: format chuẩn user@domain.ext */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}");

    // ==================== TỪ KHÓA Y TẾ NHẠY CẢM ====================

    /**
     * Danh sách từ khóa y tế/pháp lý nhạy cảm.
     * Nếu xuất hiện trong request body hoặc response, hệ thống sẽ cảnh báo.
     * Có thể thêm/bớt tùy theo yêu cầu nghiệp vụ của phòng khám.
     */
    public static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "HIV", "Ung thư", "Tuyệt mật", "Chết", "AIDS",
            "Bệnh lây truyền qua đường tình dục",
            "Tự tử", "Ý định tự sát", "Phá thai", "Chuyển giới",
            "Lạm dụng tình dục", "Quấy rối tình dục", "Xâm hại",
            "Morphine", "Lạm dụng thuốc",
            "Giết người", "Hiếp dâm", "Cướp", "Buôn bán nội tạng", "Bắt cóc"
    );

    // ==================== GOM TẤT CẢ PATTERN LẠI ĐỂ DỄ LẶP ====================

    /** Danh sách tất cả regex pattern (dùng trong vòng lặp scan) */
    public static final List<Pattern> PATTERNS = Arrays.asList(CCCD_PATTERN, PHONE_PATTERN, EMAIL_PATTERN);

    /**
     * Map tên cho từng pattern, dùng khi ghi log vi phạm.
     * Key = Pattern, Value = Tên vi phạm tương ứng.
     *
     * VD: Khi CCCD_PATTERN match → ghi violationType = "CCCD_DETECTED"
     *     Khi PHONE_PATTERN match → ghi violationType = "PHONE_DETECTED"
     *
     * Dùng LinkedHashMap để giữ thứ tự (ưu tiên check CCCD trước, vì nghiêm trọng hơn).
     */
    public static final Map<Pattern, String> PATTERN_NAMES;

    static {
        PATTERN_NAMES = new LinkedHashMap<>();
        PATTERN_NAMES.put(CCCD_PATTERN, "CCCD_DETECTED");       // Mức HIGH: CCCD là PII quan trọng nhất
        PATTERN_NAMES.put(PHONE_PATTERN, "PHONE_DETECTED");     // Mức MEDIUM: SĐT cũng là PII
        PATTERN_NAMES.put(EMAIL_PATTERN, "EMAIL_DETECTED");     // Mức LOW: email ít nhạy cảm hơn
    }
}
