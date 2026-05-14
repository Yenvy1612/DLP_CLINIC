package com.acare.backend.dlp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation đánh dấu Controller method cần được DLP bảo vệ.
 *
 * Khi method được đánh @DlpProtected, BehaviorAspect sẽ TỰ ĐỘNG chạy
 * các rule check TRƯỚC KHI method được thực thi:
 * - Kiểm tra giờ làm việc
 * - Kiểm tra rate limit
 * - Kiểm tra volume limit (nếu là download/export)
 *
 * Nếu vi phạm → throw SecurityException → trả 403 Forbidden
 *
 * Sử dụng:
 *   @DlpProtected
 *   @GetMapping("/export")
 *   public ResponseEntity<?> exportPatients() {
 *       // Method này tự động được bảo vệ bởi DLP rule engine
 *   }
 *
 *   @DlpProtected(action = "DOWNLOAD")
 *   @GetMapping("/download/{id}")
 *   public ResponseEntity<?> downloadReport(@PathVariable Long id) {
 *       // Volume limit sẽ được kiểm tra cho action "DOWNLOAD"
 *   }
 */
@Target(ElementType.METHOD)          // Chỉ dùng trên method
@Retention(RetentionPolicy.RUNTIME)  // Giữ tại runtime để AOP đọc được
public @interface DlpProtected {

    /**
     * Loại hành vi (dùng cho rule engine đánh giá rủi ro).
     * Mặc định: "VIEW"
     * Các giá trị phổ biến: "VIEW", "DOWNLOAD", "EXPORT", "EDIT"
     */
    String action() default "VIEW";
}
