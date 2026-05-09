package com.acare.backend.common;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegexPattern {
    // Dùng \b để bắt chính xác cụm số/chữ, bỏ ^ và $ để có thể quét trong chuỗi dài JSON
    public static final Pattern CCCD_PATTERN = Pattern.compile("\\b0\\d{11}\\b");
    public static final Pattern PHONE_PATTERN = Pattern.compile("\\b(84|0[3|5|7|8|9])+([0-9]{8})\\b");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}");
    
    // Danh sách từ khóa nhạy cảm
    public static final List<String> SENSITIVE_WORDS = Arrays.asList("HIV", "Ung thư", "Tuyệt mật", "Chết", "AIDS",
            "Bệnh lây truyền qua đường tình dục (STDs)",
            "Tự tử", "Ý định tự sát", "Phá thai", "Chuyển giới",
            "Lạm dụng tình dục", "Quấy rối tình dục", "Xâm hại", "Morphine", "Lạm dụng thuốc",
            "Giết người", "Hiếp dâm", "Cướp", "Buôn bán nội tạng", "Bắt cóc"
    );

    // Tất cả pattern gom lại để dễ lặp
    public static final List<Pattern> PATTERNS = Arrays.asList(CCCD_PATTERN, PHONE_PATTERN, EMAIL_PATTERN);
}


