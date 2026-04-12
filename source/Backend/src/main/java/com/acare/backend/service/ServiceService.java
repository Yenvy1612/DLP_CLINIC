package com.acare.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.entity.Specialty;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final SpecialtyService specialtyService;

    public List<com.acare.backend.entity.Service> getAllServices() {
        List<com.acare.backend.entity.Service> services = serviceRepository.findAll();
        services.sort((s1, s2) -> s2.getPrice().compareTo(s1.getPrice()));
        return services;
    }

    public com.acare.backend.entity.Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu voi id=" + id));
    }

    public ApiResponse<com.acare.backend.entity.Service> addService(com.acare.backend.entity.Service request) {
        com.acare.backend.entity.Service normalized = request.withDefaults();

        if (normalized.getName() == null || normalized.getName().isBlank()) {
            return ApiResponse.fail(400, "SERVICE NAME IS REQUIRED", normalized);
        }
        if (normalized.getPrice() == null || normalized.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return ApiResponse.fail(400, "SERVICE PRICE IS INVALID", normalized);
        }
        if (normalized.getDurationMin() == null || normalized.getDurationMin() <= 0) {
            return ApiResponse.fail(400, "SERVICE DURATION IS INVALID", normalized);
        }
        if (normalized.getSpecialtyId() == null) {
            return ApiResponse.fail(400, "SERVICE SPECIALTY IS REQUIRED", normalized);
        }

        Specialty specialty = specialtyService.getRequiredActiveById(normalized.getSpecialtyId());
        normalized = normalized.toBuilder()
                .specialtyId(specialty.getId())
                .department(normalizeDepartment(specialty.getCode()))
                .build();

        List<com.acare.backend.entity.Service> existed = serviceRepository.findByName(normalized.getName().trim());
        if (!existed.isEmpty()) {
            return ApiResponse.fail(409, "SERVICE ALREADY EXIST", normalized);
        }

        com.acare.backend.entity.Service created = serviceRepository.save(normalized);
        return ApiResponse.created("ADD SERVICE SUCCESSFULLY", created);
    }

    public ApiResponse<com.acare.backend.entity.Service> updateService(Long id, com.acare.backend.entity.Service update) {
        com.acare.backend.entity.Service service = getById(id).mergeFrom(update).withDefaults();

        if (service.getSpecialtyId() == null) {
            return ApiResponse.fail(400, "SERVICE SPECIALTY IS REQUIRED", service);
        }

        Specialty specialty = specialtyService.getRequiredActiveById(service.getSpecialtyId());
        service = service.toBuilder()
            .specialtyId(specialty.getId())
            .department(normalizeDepartment(specialty.getCode()))
                .build();
        com.acare.backend.entity.Service updated = serviceRepository.save(service);
        return ApiResponse.ok("UPDATE SERVICE SUCCESSFULLY", updated);
    }

    public ApiResponse<com.acare.backend.entity.Service> deleteService(Long id) {
        com.acare.backend.entity.Service service = getById(id);
        serviceRepository.deleteById(id);
        return ApiResponse.ok("DELETE SERVICE SUCCESSFULLY", service);
    }

    public List<com.acare.backend.entity.Service> searchServices(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<com.acare.backend.entity.Service> services = serviceRepository.findAll();

        if (name != null && !name.trim().isEmpty()) {
            services = services.stream()
                    .filter(service -> service.getName() != null && 
                            service.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            services = services.stream()
                    .filter(service -> service.getPrice() != null && service.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            services = services.stream()
                    .filter(service -> service.getPrice() != null && service.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return services;
    }

    private String normalizeDepartment(String department) {
        if (department == null || department.isBlank()) {
            return "GENERAL";
        }

        return department.trim().toUpperCase(Locale.ROOT);
    }
}
