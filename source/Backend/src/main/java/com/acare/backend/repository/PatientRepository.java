package com.acare.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acare.backend.entity.Patient;



public interface PatientRepository extends JpaRepository<Patient,Long>  {
    
}
