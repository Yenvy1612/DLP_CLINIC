package com.acare.backend.repository;

import java.util.List;
import java.util.Optional;
import com.acare.backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>{
    List<ActivityLog> findTop10ByOrderByTimeDesc();

    List<ActivityLog> findTop20ByTargetUserIdOrderByTimeDesc(Long targetUserId);

    long countByTargetUserId(Long targetUserId);

    Optional<ActivityLog> findByIdAndTargetUserId(Long id, Long targetUserId);

    List<ActivityLog> findTop20ByMessageContainingOrderByTimeDesc(String token);

    List<ActivityLog> findTop20ByTypeOrderByTimeDesc(String type);

    long countByType(String type);

    Optional<ActivityLog> findByIdAndType(Long id, String type);
}
