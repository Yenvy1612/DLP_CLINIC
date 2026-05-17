package com.acare.backend.repository;

import com.acare.backend.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Optional<Agent> findByDeviceId(String deviceId);
    boolean existsByDeviceIdAndTrustedTrue(String deviceId);
}
