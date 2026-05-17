package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.entity.DlpLog;
import com.acare.backend.repository.DlpLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dlp-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DlpLogController {

    private final DlpLogRepository dlpLogRepository;

    @GetMapping
    public ApiResponse<Page<DlpLog>> getAllDlpLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Show newest ingested logs first so freshly synced mobile events are visible immediately.
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DlpLog> logs = dlpLogRepository.findAll(pageable);
        return ApiResponse.ok("DLP Logs retrieved successfully", logs);
    }
}
