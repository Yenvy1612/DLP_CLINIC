package com.acare.backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByName(String name);
    List<Service> findAllByNameContainingIgnoreCase(String name);
    List<Service> findByActiveTrue();
    List<Service> findByPriceBetween(BigDecimal min, BigDecimal max);
}
