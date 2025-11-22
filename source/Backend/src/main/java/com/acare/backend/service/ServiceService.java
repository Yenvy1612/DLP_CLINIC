package com.acare.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acare.backend.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<com.acare.backend.entity.Service> searchServices(String name, Double minPrice, Double maxPrice) {
        // Lấy tất cả services
        List<com.acare.backend.entity.Service> services = serviceRepository.findAll();

        // Search theo tên dịch vụ
        if (name != null && !name.trim().isEmpty()) {
            services = services.stream()
                    .filter(service -> service.getName() != null && 
                            service.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Search theo khoảng giá
        if (minPrice != null) {
            services = services.stream()
                    .filter(service -> service.getPrice() != null && service.getPrice() >= minPrice)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            services = services.stream()
                    .filter(service -> service.getPrice() != null && service.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        return services;
    }
}
