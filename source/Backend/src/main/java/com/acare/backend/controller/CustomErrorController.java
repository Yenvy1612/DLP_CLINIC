package com.acare.backend.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ResponseDTO;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    public ResponseEntity<ResponseDTO> handleError() {
        return ResponseEntity.ok(
                new ResponseDTO(404, true, "Not found", "Trang ban truy cap khong ton tai"));
    }
}
