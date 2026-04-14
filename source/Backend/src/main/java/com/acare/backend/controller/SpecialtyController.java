package com.acare.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.specialty.SpecialtyResponse;
import com.acare.backend.service.SpecialtyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> getActiveSpecialties() {
        return ResponseEntity.ok(specialtyService.getActiveSpecialties().stream().map(SpecialtyResponse::from).toList());
    }
}
