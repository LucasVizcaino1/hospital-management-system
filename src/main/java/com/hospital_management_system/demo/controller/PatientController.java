package com.hospital_management_system.demo.controller;

import com.hospital_management_system.demo.dto.request.PatientRequestDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Create a new patient")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto requestDto) {
        log.info("Create patient request received: {}", requestDto);
        PatientResponseDto response = patientService.createPatient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all patients (paginated)")
    @GetMapping
    public ResponseEntity<Page<PatientResponseDto>> getAllPatients(@ParameterObject Pageable pageable) {
        log.info("Listing all patients, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @Operation(summary = "List active patients")
    @GetMapping("/active")
    public ResponseEntity<List<PatientResponseDto>> getActivePatients() {
        log.info("Listing active patients");
        List<PatientResponseDto> patients = patientService.listAssets();
        return ResponseEntity.ok(patients);
    }

    @Operation(summary = "List patients by state (paginated)")
    @GetMapping("/state/{state}")
    public ResponseEntity<Page<PatientResponseDto>> getPatientsByState(
            @PathVariable State state,
            @ParameterObject Pageable pageable) {
        log.info("Listing patients with state={}, page={} size={}",
                state, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(patientService.getAllPatientsByState(state, pageable));
    }

    @Operation(summary = "Get a patient by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatientById(@PathVariable Long id) {
        log.info("Getting patient with id={}", id);
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @Operation(summary = "Update a patient")
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDto requestDto) {
        log.info("Update patient request for id={}: {}", id, requestDto);
        PatientResponseDto response = patientService.updatePatient(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a patient")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("Delete patient request for id={}", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}