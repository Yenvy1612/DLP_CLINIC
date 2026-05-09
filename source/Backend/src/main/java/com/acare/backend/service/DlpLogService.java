package com.acare.backend.service;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.DlpLog;
import com.acare.backend.repository.DlpLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DlpLogService {
    private final DlpLogRepository dlpLogRepository;
    public void add(DlpLog dlpLog) {
        dlpLogRepository.save(dlpLog);
    }

}

