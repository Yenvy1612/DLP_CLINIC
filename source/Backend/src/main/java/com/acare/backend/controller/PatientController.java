package com.acare.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.entity.Patient;
import com.acare.backend.service.PatientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    @PostMapping
    public String addPatient(@RequestBody Patient newPatient) {
        patientService.addPatient(newPatient);
        return "Done";
    }
    @GetMapping
    public List<Patient> getPatient() {
        return patientService.getPatient();
    }
}
