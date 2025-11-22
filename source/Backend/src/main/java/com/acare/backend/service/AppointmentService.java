package com.acare.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.Room;
import com.acare.backend.entity.User;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.RoomRepository;
import com.acare.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public List<Appointment> filterAppointments(
            String doctorName,
            String patientName,
            LocalDate appointmentDate,
            String status,
            String roomName) {

        // Lấy tất cả appointments
        List<Appointment> appointments = appointmentRepository.findAll();

        // Filter theo tên bác sĩ
        if (doctorName != null && !doctorName.trim().isEmpty()) {
            List<Long> doctorIds = userRepository.findByRole("DOCTOR").stream()
                    .filter(doctor -> doctor.getFullName().toLowerCase().contains(doctorName.toLowerCase()))
                    .map(User::getId)
                    .collect(Collectors.toList());
            
            appointments = appointments.stream()
                    .filter(apt -> doctorIds.contains(apt.getDoctorId()))
                    .collect(Collectors.toList());
        }

        // Filter theo tên bệnh nhân
        if (patientName != null && !patientName.trim().isEmpty()) {
            List<Long> patientIds = userRepository.findByRole("PATIENT").stream()
                    .filter(patient -> patient.getFullName().toLowerCase().contains(patientName.toLowerCase()))
                    .map(User::getId)
                    .collect(Collectors.toList());
            
            appointments = appointments.stream()
                    .filter(apt -> patientIds.contains(apt.getPatientId()))
                    .collect(Collectors.toList());
        }

        // Filter theo ngày khám
        if (appointmentDate != null) {
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();
            LocalDateTime endOfDay = appointmentDate.atTime(23, 59, 59);
            
            appointments = appointments.stream()
                    .filter(apt -> apt.getStartTime() != null 
                            && !apt.getStartTime().isBefore(startOfDay) 
                            && !apt.getStartTime().isAfter(endOfDay))
                    .collect(Collectors.toList());
        }

        // Filter theo trạng thái
        if (status != null && !status.trim().isEmpty()) {
            appointments = appointments.stream()
                    .filter(apt -> apt.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        // Filter theo tên phòng
        if (roomName != null && !roomName.trim().isEmpty()) {
            List<Long> roomIds = roomRepository.findAll().stream()
                    .filter(room -> room.getName().toLowerCase().contains(roomName.toLowerCase()))
                    .map(Room::getId)
                    .collect(Collectors.toList());
            
            appointments = appointments.stream()
                    .filter(apt -> apt.getRoomId() != null && roomIds.contains(apt.getRoomId()))
                    .collect(Collectors.toList());
        }

        return appointments;
    }
}
