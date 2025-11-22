package com.acare.backend.repository;

import java.util.List;
import com.acare.backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>{
    List<ActivityLog> findTop10ByOrderByTimeDesc();
}
