package com.acare.backend.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Táº­p há»£p cÃ¡c Regex Pattern dÃ¹ng Ä‘á»ƒ quÃ©t dá»¯ liá»‡u nháº¡y cáº£m.
 *
 * ÄÆ°á»£c sá»­ dá»¥ng bá»Ÿi DlpScannerService Ä‘á»ƒ:
 * 1. PhÃ¡t hiá»‡n thÃ´ng tin cÃ¡ nhÃ¢n (PII): CCCD, SÄT, email
 * 2. PhÃ¡t hiá»‡n tá»« khÃ³a y táº¿ nháº¡y cáº£m: HIV, Ung thÆ°, ...
 *
 * LÆ°u Ã½: DÃ¹ng \b (word boundary) Ä‘á»ƒ trÃ¡nh báº¯t nháº§m chuá»—i con.
 * VD: "0123456789012" sáº½ match CCCD, nhÆ°ng "abc0123456789012xyz" cÅ©ng match
 *     vÃ¬ \b báº¯t ranh giá»›i giá»¯a chá»¯ vÃ  sá»‘.
 */
public class RegexPattern {

    // ==================== PATTERN NHáº¬N DIá»†N THÃ”NG TIN CÃ NHÃ‚N (PII) ====================

    /** CCCD/CMND: 12 chá»¯ sá»‘ báº¯t Ä‘áº§u báº±ng 0 (VD: 012345678901) */
    public static final Pattern CCCD_PATTERN = Pattern.compile("\\b0\\d{11}\\b");

    /** Sá»‘ Ä‘iá»‡n thoáº¡i Viá»‡t Nam: báº¯t Ä‘áº§u báº±ng 84 hoáº·c 03/05/07/08/09 + 8 chá»¯ sá»‘ */
    public static final Pattern PHONE_PATTERN = Pattern.compile("\\b(84|0[3|5|7|8|9])+([0-9]{8})\\b");

    /** Email: format chuáº©n user@domain.ext */
    public static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}");

    // ==================== Tá»ª KHÃ“A Y Táº¾ NHáº Y Cáº¢M ====================

    /**
     * Danh sÃ¡ch tá»« khÃ³a y táº¿/phÃ¡p lÃ½ nháº¡y cáº£m.
     * Náº¿u xuáº¥t hiá»‡n trong request body hoáº·c response, há»‡ thá»‘ng sáº½ cáº£nh bÃ¡o.
     * CÃ³ thá»ƒ thÃªm/bá»›t tÃ¹y theo yÃªu cáº§u nghiá»‡p vá»¥ cá»§a phÃ²ng khÃ¡m.
     */
    public static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "HIV", "Ung thu", "Tuyet mat", "Chet", "AIDS",
            "Bá»‡nh lÃ¢y truyá»n qua Ä‘Æ°á»ng tÃ¬nh dá»¥c",
            "Tá»± tá»­", "Ã Ä‘á»‹nh tá»± sÃ¡t", "PhÃ¡ thai", "Chuyá»ƒn giá»›i",
            "Láº¡m dá»¥ng tÃ¬nh dá»¥c", "Quáº¥y rá»‘i tÃ¬nh dá»¥c", "XÃ¢m háº¡i",
            "Morphine", "Láº¡m dá»¥ng thuá»‘c",
            "Giáº¿t ngÆ°á»i", "Hiáº¿p dÃ¢m", "CÆ°á»›p", "BuÃ´n bÃ¡n ná»™i táº¡ng", "Báº¯t cÃ³c"
    );

    // ==================== GOM Táº¤T Cáº¢ PATTERN Láº I Äá»‚ Dá»„ Láº¶P ====================

    /** Danh sÃ¡ch táº¥t cáº£ regex pattern (dÃ¹ng trong vÃ²ng láº·p scan) */
    public static final List<Pattern> PATTERNS = Arrays.asList(CCCD_PATTERN, PHONE_PATTERN, EMAIL_PATTERN);

    /**
     * Map tÃªn cho tá»«ng pattern, dÃ¹ng khi ghi log vi pháº¡m.
     * Key = Pattern, Value = TÃªn vi pháº¡m tÆ°Æ¡ng á»©ng.
     *
     * VD: Khi CCCD_PATTERN match â†’ ghi violationType = "CCCD_DETECTED"
     *     Khi PHONE_PATTERN match â†’ ghi violationType = "PHONE_DETECTED"
     *
     * DÃ¹ng LinkedHashMap Ä‘á»ƒ giá»¯ thá»© tá»± (Æ°u tiÃªn check CCCD trÆ°á»›c, vÃ¬ nghiÃªm trá»ng hÆ¡n).
     */
    public static final Map<Pattern, String> PATTERN_NAMES;

    static {
        PATTERN_NAMES = new LinkedHashMap<>();
        PATTERN_NAMES.put(CCCD_PATTERN, "CCCD_DETECTED");       // Má»©c HIGH: CCCD lÃ  PII quan trá»ng nháº¥t
        PATTERN_NAMES.put(PHONE_PATTERN, "PHONE_DETECTED");     // Má»©c MEDIUM: SÄT cÅ©ng lÃ  PII
        PATTERN_NAMES.put(EMAIL_PATTERN, "EMAIL_DETECTED");     // Má»©c LOW: email Ã­t nháº¡y cáº£m hÆ¡n
    }
}

