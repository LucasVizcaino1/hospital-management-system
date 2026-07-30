package com.hospital_management_system.demo.service;

import com.hospital_management_system.demo.dto.request.AppointmentRequestDto;
import com.hospital_management_system.demo.dto.response.AppointmentResponseDto;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AppointmentService {
    AppointmentResponseDto createAppointment(AppointmentRequestDto dto);
    AppointmentResponseDto getAppointmentById(Long id);
    Page<AppointmentResponseDto> getAllAppointments(Pageable pageable);
    Page<AppointmentResponseDto> getAppointmentsByPatient(Long patientId, Pageable pageable);
    Page<AppointmentResponseDto> getAppointmentsByEmployee(Long employeeId, Pageable pageable);
    Page<AppointmentResponseDto> getAppointmentsByStatus(State state, Pageable pageable);
    Page<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<AppointmentResponseDto> searchByReason(String reason, Pageable pageable);
    AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto dto);
    void deleteAppointment(Long id);
    Page<AppointmentResponseDto> getAuthenticatedPatientAttentions(String username, Pageable pageable);
}