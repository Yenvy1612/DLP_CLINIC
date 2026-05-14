package com.acare.backend.entity;



import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import com.acare.backend.utils.AttributeEncryptor;

@Entity
@Getter
@Setter
@Table(name="patients")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Patient {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Convert(converter = AttributeEncryptor.class)
    private String cccd;
    private String phone;
    private String email;
    private String medicalHistory;
    private LocalDate dob;

}
