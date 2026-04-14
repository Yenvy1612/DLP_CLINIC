package com.acare.backend.controller;

import com.acare.backend.dto.ApiResponse;
import com.acare.backend.dto.ApiResponseMapper;
import com.acare.backend.dto.service.ServiceRequest;
import com.acare.backend.dto.service.ServiceResponse;
import com.acare.backend.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getService(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<ServiceResponse> services;
        if ((name != null && !name.isBlank()) || minPrice != null || maxPrice != null) {
            services = serviceService.searchServices(name, minPrice, maxPrice).stream()
                    .map(ServiceResponse::from)
                    .toList();
        } else {
            services = serviceService.getAllServices().stream()
                    .map(ServiceResponse::from)
                    .toList();
        }

        services = services.stream()
                .sorted(Comparator.comparing(ServiceResponse::getPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed())
                .toList();

        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceResponse>> addService(@RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponseMapper.map(serviceService.addService(request.toEntity()), ServiceResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(ServiceResponse.from(serviceService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(@PathVariable Long id, @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(ApiResponseMapper.map(serviceService.updateService(id, request.toEntity()), ServiceResponse::from));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> updateService(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseMapper.map(serviceService.deleteService(id), ServiceResponse::from));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceResponse>> searchService(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return getService(name, minPrice, maxPrice);
    }
}
