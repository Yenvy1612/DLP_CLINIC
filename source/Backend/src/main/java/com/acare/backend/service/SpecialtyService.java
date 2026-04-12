package com.acare.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.Specialty;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.repository.SpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public List<Specialty> getActiveSpecialties() {
        return specialtyRepository.findByActiveTrueOrderByNameAsc();
    }

    public Specialty getRequiredActiveById(Long specialtyId) {
        if (specialtyId == null) {
            throw new BadRequestException("SPECIALTY IS REQUIRED");
        }

        return specialtyRepository.findByIdAndActiveTrue(specialtyId)
                .orElseThrow(() -> new BadRequestException("SPECIALTY IS INVALID"));
    }

    public Specialty getRequiredByCode(String code) {
        return specialtyRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BadRequestException("SPECIALTY CODE IS INVALID"));
    }
}
