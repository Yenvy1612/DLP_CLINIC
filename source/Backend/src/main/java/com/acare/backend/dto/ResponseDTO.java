package com.acare.backend.dto;

import lombok.Data;


// Trả về 1 thông điệp dữ liệu chung của cả dự án 
@Data
public class ResponseDTO {
    private int status;
    private boolean success;
    private String message;
    private Object data;
    public ResponseDTO(int status, boolean success, String message, Object data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
