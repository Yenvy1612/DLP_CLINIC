package com.acare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.DlpLog;


public interface DlpLogRepository extends JpaRepository<DlpLog, Long> {
    
}

