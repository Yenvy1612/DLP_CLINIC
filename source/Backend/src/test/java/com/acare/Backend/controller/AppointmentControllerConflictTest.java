package com.acare.Backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.acare.backend.controller.AppointmentController;
import com.acare.backend.exception.ConflictException;
import com.acare.backend.exception.GlobalExceptionHandler;
import com.acare.backend.service.AppointmentService;

@WebMvcTest(controllers = AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerConflictTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    void createAppointment_whenSlotConflict_returns409() throws Exception {
        when(appointmentService.createAppointment(any(com.acare.backend.dto.appointment.AppointmentCreateRequest.class)))
                .thenThrow(new ConflictException("Khung gio vua duoc dat boi nguoi khac, vui long chon khung gio khac"));

        String payload = """
                {
                  "patientId": 1,
                  "serviceId": 2,
                  "startTime": "2026-04-20T09:00:00"
                }
                """;

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Khung gio vua duoc dat boi nguoi khac, vui long chon khung gio khac"));
    }
}
