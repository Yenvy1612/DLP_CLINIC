package com.acare.backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.acare.backend.entity.Patient;
import com.acare.backend.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public void addPatient(Patient newPatient) {
        Patient patient = Patient.builder()
                .fullName(newPatient.getFullName())
                .cccd(passwordEncoder.encode(newPatient.getCccd()))
                .phone(newPatient.getPhone())
                .medicalHistory(newPatient.getMedicalHistory())
                .build();
        patientRepository.save(patient);
    }
    public List<Patient> getPatient() {
        return patientRepository.findAll();
    }
}

