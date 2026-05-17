package com.acare.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;
    private boolean success;
    private String message;
    private T data;

    // ================= SUCCESS =================

    // Có message + data
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, true, message, data);
    }

    // Chỉ có data
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, true, "Success", data);
    }

    // Chỉ có message
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(200, true, message, null);
    }

    // ================= CREATED =================

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, true, message, data);
    }

    // ================= FAIL =================

    public static <T> ApiResponse<T> fail(int status, String message, T data) {
        return new ApiResponse<>(status, false, message, data);
    }

    // Không có data
    public static <T> ApiResponse<T> fail(int status, String message) {
        return new ApiResponse<>(status, false, message, null);
    }
}