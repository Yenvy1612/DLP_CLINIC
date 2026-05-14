package com.acare.backend.entity.enums;

/**
 * Mức độ rủi ro của vi phạm DLP.
 *
 * LOW      - Vi phạm nhẹ, chỉ cần ghi log (VD: email xuất hiện trong response)
 * MEDIUM   - Vi phạm trung bình, cần theo dõi (VD: số điện thoại trong body request)
 * HIGH     - Vi phạm nghiêm trọng, mask dữ liệu + cảnh báo admin (VD: CCCD bệnh nhân)
 * CRITICAL - Vi phạm rất nghiêm trọng, block request + tự động khóa user (VD: download hàng loạt)
 */
public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
