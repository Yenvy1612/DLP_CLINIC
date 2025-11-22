package com.acare.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acare.backend.dto.ResponseDTO;
import com.acare.backend.entity.Appointment;
import com.acare.backend.entity.User;
import com.acare.backend.repository.AppointmentRepository;
import com.acare.backend.repository.UserRepository;
import com.acare.backend.service.ActivityLogService;
import com.acare.backend.service.AppointmentService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final ActivityLogService activityLogService;

    private final AppointmentRepository appointmentRepository;

    private final UserRepository userRepository;

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<ResponseDTO> createAppointment(@RequestBody Appointment appointment) {
        if (appointmentRepository.existsByDoctorIdAndStartTime(appointment.getDoctorId(), appointment.getStartTime())) return ResponseEntity.ok(new ResponseDTO(404, false, "Bác sĩ có lịch vào khung giờ này.", null));
        if (appointmentRepository.existsByRoomIdAndStartTime(appointment.getRoomId(), appointment.getStartTime())) return ResponseEntity.ok(new ResponseDTO(404, false, "Phòng có lịch vào khung giờ này.", null));
        Appointment saved = appointmentRepository.save(appointment);

        /* */
        Optional<User> user = userRepository.findById(saved.getPatientId());
        activityLogService.add("APPOINTMENT", "Bệnh nhân " + user.get().getFullName() + " vừa đặt lịch thành công");
        return ResponseEntity.ok(new ResponseDTO(201, true, "Đã đặt lịch hẹn", saved));
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @GetMapping("/today")
    public ResponseEntity<List<Appointment>> getTodayAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> apptToday = appointmentRepository.findByStartTimeBetween(start, end);
        apptToday.sort(Comparator.comparing((Appointment a) -> a.getCreatedAt()));
        return ResponseEntity.ok(apptToday);
    }

    @GetMapping("/today/pending")
    public ResponseEntity<List<Appointment>> getTodayPendingAppointments() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> apptToday = appointmentRepository.findByStartTimeBetweenAndStatus(start, end, "PENDING");
        apptToday.sort(Comparator.comparing((Appointment a) -> a.getCreatedAt()));
        return ResponseEntity.ok(apptToday);
    }

    @GetMapping("/month/done")
    public ResponseEntity<List<Appointment>> getMonthDoneAppointments() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> appt = appointmentRepository.findByStartTimeBetweenAndStatus(start, end, "DONE");
        return ResponseEntity.ok(appt);
    }

    @GetMapping("/month/done/{doctorId}")
    public ResponseEntity<List<Appointment>> getMonthDoneAppointmentsByDoctorId(@PathVariable Long doctorId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);
        List<Appointment> appt = appointmentRepository.findByStartTimeBetweenAndStatusAndDoctorId(start, end, "DONE", doctorId);
        return ResponseEntity.ok(appt);
    }

    @GetMapping("/{id}")
    public Optional<Appointment> getAllAppointments(@PathVariable Long id) {
        return appointmentRepository.findById(id);
    }

    @GetMapping("/pending/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPendingAppoinmentsByPatientId(@PathVariable Long patientId) {
        if (patientId == null) return ResponseEntity.ok(appointmentRepository.findAll());
        List<Appointment> appointments = appointmentRepository.findByStatusAndPatientId("PENDING", patientId);
        appointments.sort(Comparator.comparing((Appointment a) -> a.getStartTime()));
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/not-pending/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getDoneAppoinmentsByPatientId(@PathVariable Long patientId) {
        if (patientId == null) return ResponseEntity.ok(appointmentRepository.findAll());
        List<Appointment> doneAppointments = appointmentRepository.findByStatusAndPatientId("DONE", patientId);
        List<Appointment> cancelledAppointments = appointmentRepository.findByStatusAndPatientId("CANCELLED", patientId);
        doneAppointments.addAll(cancelledAppointments);
        doneAppointments.sort(Comparator.comparing((Appointment a) -> a.getStartTime()));
        return ResponseEntity.ok(doneAppointments);
    }

    @GetMapping("/pending/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getPendingAppoinmentsByDoctorId(@PathVariable Long doctorId) {
        if (doctorId == null) return ResponseEntity.ok(appointmentRepository.findAll());
        List<Appointment> appointments = appointmentRepository.findByStatusAndDoctorId("PENDING", doctorId);
        appointments.sort(Comparator.comparing((Appointment a) -> a.getStartTime()));
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAllAppoinmentsByDoctorId(@PathVariable Long doctorId) {
        if (doctorId != null) return ResponseEntity.ok(appointmentRepository.findByDoctorId(doctorId));
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointmentById(@PathVariable Long id) {
        if (id == null) return ResponseEntity.ok("DELETE FAILED");
        Optional<Appointment> apt = appointmentRepository.findById(id);
        Optional<User> user = userRepository.findById(apt.get().getPatientId());
        appointmentRepository.deleteById(id);
        activityLogService.add("APPOINTMENT", "Bệnh nhân " + user.get().getFullName() + " Đã hủy lịch hẹn " + id);
        return ResponseEntity.ok("DELETE SUCCESSFULLY");
    }

    @PatchMapping("/done/{id}")
    public ResponseEntity<String> changeAppointmentStatusFromPendingToDone(@PathVariable Long id) {
        Optional<Appointment> appointments = appointmentRepository.findById(id);
        Appointment updateAppointment = appointments.get();
        updateAppointment.setStatus("DONE");
        appointmentRepository.save(updateAppointment);
        return ResponseEntity.ok("UPDATE APPOINTMENT STATUS SUCCESSFULLY");
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> changeAppointmentStatusFromPendingToCanceled(@PathVariable Long id) {
        Optional<Appointment> appointments = appointmentRepository.findById(id);
        Appointment updateAppointment = appointments.get();
        updateAppointment.setStatus("CANCELLED");
        appointmentRepository.save(updateAppointment);
        return ResponseEntity.ok("UPDATE APPOINTMENT STATUS SUCCESSFULLY");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment update) {
        Optional<Appointment> appointments = appointmentRepository.findById(id);
        if (appointments.isEmpty()) return ResponseEntity.ok(null);
        Appointment appointment = appointments.get();
        if (update.getDoctorId() != null) appointment.setDoctorId(update.getDoctorId());
        if (update.getRoomId() != null) appointment.setRoomId(update.getRoomId());
        if (update.getStartTime() != null) appointment.setStartTime(update.getStartTime());
        if (update.getNote() != null) appointment.setNote(update.getNote());
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(appointment);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<Appointment>> filterAppointments(
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate appointmentDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roomName) {
        
        List<Appointment> filteredAppointments = appointmentService.filterAppointments(
                doctorName, patientName, appointmentDate, status, roomName);
        
        // Sort by start time
        filteredAppointments.sort(Comparator.comparing(Appointment::getStartTime));
        
        return ResponseEntity.ok(filteredAppointments);
    }
}
