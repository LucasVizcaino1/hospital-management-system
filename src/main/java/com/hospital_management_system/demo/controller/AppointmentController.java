package com.hospital_management_system.demo.controller;


import com.hospital_management_system.demo.dto.request.AppointmentRequestDto;
import com.hospital_management_system.demo.dto.response.AppointmentResponseDto;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Create a new medical appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Patient or employee not found")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(@Valid @RequestBody AppointmentRequestDto requestDto) {
        log.info("Create appointment request received: {}", requestDto);
        AppointmentResponseDto response = appointmentService.createAppointment(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all appointments (paginated)")
    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDto>> getAllAppointments(@ParameterObject Pageable pageable) {
        log.info("Listing all appointments, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable));
    }

    @Operation(summary = "List appointments by date range (paginated)")
    @GetMapping("/dates")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @ParameterObject Pageable pageable) {
        log.info("Searching appointments between {} and {}, page={} size={}",
                startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(startDate, endDate, pageable));
    }

    @Operation(summary = "Search appointments by reason (paginated)")
    @GetMapping("/search")
    public ResponseEntity<Page<AppointmentResponseDto>> searchByReason(
            @RequestParam String reason,
            @ParameterObject Pageable pageable) {
        log.info("Searching appointments with reason='{}', page={} size={}",
                reason, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.searchByReason(reason, pageable));
    }

    @Operation(summary = "List appointments by state (paginated)")
    @GetMapping("/status")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByStatus(
            @RequestParam State status,
            @ParameterObject Pageable pageable) {
        log.info("Listing appointments with status={}, page={} size={}",
                status, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status, pageable));
    }

    @Operation(summary = "List appointments of a specific patient (paginated)")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByPatient(
            @PathVariable Long patientId,
            @ParameterObject Pageable pageable) {
        log.info("Listing appointments for patient id={}, page={} size={}",
                patientId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId, pageable));
    }

    @Operation(summary = "List appointments of a specific employee (paginated)")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<AppointmentResponseDto>> getAppointmentsByEmployee(
            @PathVariable Long employeeId,
            @ParameterObject Pageable pageable) {
        log.info("Listing appointments for employee id={}, page={} size={}",
                employeeId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(appointmentService.getAppointmentsByEmployee(employeeId, pageable));
    }

    @Operation(summary = "Get an appointment by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(@PathVariable Long id) {
        log.info("Getting appointment with id={}", id);
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @Operation(summary = "Update an existing appointment")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDto requestDto) {
        log.info("Update appointment request for id={}: {}", id, requestDto);
        AppointmentResponseDto response = appointmentService.updateAppointment(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete an appointment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        log.info("Delete appointment request for id={}", id);
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}