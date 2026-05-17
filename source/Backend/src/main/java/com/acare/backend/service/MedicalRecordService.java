package com.acare.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.acare.backend.dto.medicalrecord.MedicalRecordResponse;
import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.MedicalRecord;
import com.acare.backend.entity.MedicalRecordServiceItem;
import com.acare.backend.entity.PatientProfile;
import com.acare.backend.entity.User;
import com.acare.backend.entity.enums.UserRole;
import com.acare.backend.exception.BadRequestException;
import com.acare.backend.exception.ResourceNotFoundException;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.MedicalRecordRepository;
import com.acare.backend.repository.MedicalRecordServiceRepository;
import com.acare.backend.repository.PatientProfileRepository;
import com.acare.backend.repository.ServiceRepository;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordServiceRepository medicalRecordServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final ServiceRepository serviceRepository;
    private final ActivityLogService activityLogService;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getAppointmentId() == null) {
            throw new BadRequestException("Appointment id khong duoc de trong");
        }

        if (medicalRecordRepository.existsByAppointmentId(medicalRecord.getAppointmentId())) {
            throw new BadRequestException("Lich hen nay da co ho so benh an");
        }

        Appointment appointment = appointmentRepository.findById(medicalRecord.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay lich hen"));

        Long patientId = medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : appointment.getPatientId();
        Long doctorId = medicalRecord.getDoctorId() != null ? medicalRecord.getDoctorId() : appointment.getDoctorId();

        validateRecordActors(patientId, doctorId);

        MedicalRecord prepared = medicalRecord.prepareForCreate(patientId, doctorId, generateRecordCode());
        MedicalRecord saved = medicalRecordRepository.save(prepared);
        activityLogService.add("MEDICAL_RECORD", "Tao ho so benh an " + saved.getRecordCode());
        return saved;
    }

    public MedicalRecord getById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so benh an"));
    }

    public MedicalRecord getByAppointmentId(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so benh an cua lich hen"));
    }

    public List<MedicalRecord> getByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<MedicalRecord> getByDoctorId(Long doctorId) {
        return medicalRecordRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord update) {
        MedicalRecord record = getById(id).mergeFrom(update);
        return medicalRecordRepository.save(record);
    }

    public MedicalRecordServiceItem addServiceToRecord(Long medicalRecordId, MedicalRecordServiceItem request) {
        getById(medicalRecordId);

        com.acare.backend.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dich vu"));

        MedicalRecordServiceItem prepared = request.prepareForRecord(medicalRecordId, service.getPrice());
        return medicalRecordServiceRepository.save(prepared);
    }

    public List<MedicalRecordServiceItem> getServicesByRecordId(Long medicalRecordId) {
        return medicalRecordServiceRepository.findByMedicalRecordId(medicalRecordId);
    }

    public MedicalRecordResponse toResponse(MedicalRecord record) {
        MedicalRecordResponse response = MedicalRecordResponse.from(record);
        if (response == null) return null;

        User doctor = userRepository.findById(record.getDoctorId()).orElse(null);
        response.setDoctorName(doctor != null ? doctor.getFullName() : null);

        User patient = userRepository.findById(record.getPatientId()).orElse(null);
        PatientProfile profile = patientProfileRepository.findByUserId(record.getPatientId()).orElse(null);

        response.setPatientFullName(patient != null ? patient.getFullName() : null);
        response.setPatientEmail(patient != null ? patient.getEmail() : null);
        response.setPatientPhone(patient != null ? patient.getPhone() : null);
        response.setPatientIdNumber(patient != null ? patient.getIdNumber() : null);

        if (profile != null) {
            response.setBloodType(profile.getBloodType());
            response.setInsuranceNumber(profile.getInsuranceNumber());
            response.setAllergies(profile.getAllergies());
            response.setChronicConditions(profile.getChronicConditions());
            response.setEmergencyContactName(profile.getEmergencyContactName());
            response.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        }
        return response;
    }

    private void validateRecordActors(Long patientId, Long doctorId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay benh nhan"));
        if (patient.getRole() != UserRole.PATIENT) {
            throw new BadRequestException("Patient id khong hop le");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bac si"));
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new BadRequestException("Doctor id khong hop le");
        }
    }

    private String generateRecordCode() {
        return "MR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
