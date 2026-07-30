package com.hospital_management_system.demo.controller;

import com.hospital_management_system.demo.dto.request.SpecialtyRequestDto;
import com.hospital_management_system.demo.dto.response.SpecialtyResponseDto;
import com.hospital_management_system.demo.service.SpecialtyService;
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

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Slf4j
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @Operation(summary = "Create a new specialty")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Specialty created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> createSpecialty(@Valid @RequestBody SpecialtyRequestDto requestDto) {
        log.info("Create specialty request received: {}", requestDto);
        SpecialtyResponseDto response = specialtyService.createSpecialty(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "List all specialties (paginated)")
    @GetMapping
    public ResponseEntity<Page<SpecialtyResponseDto>> getAllSpecialties(@ParameterObject Pageable pageable) {
        log.info("Listing all specialties, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(specialtyService.getAllSpecialties(pageable));
    }

    @Operation(summary = "Search specialties by name (paginated)")
    @GetMapping("/name")
    public ResponseEntity<Page<SpecialtyResponseDto>> getSpecialtiesByName(
            @RequestParam String name,
            @ParameterObject Pageable pageable) {
        log.info("Searching specialties with name={}, page={} size={}",
                name, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(specialtyService.searchByName(name, pageable));
    }

    @Operation(summary = "List specialties by state (paginated)")
    @GetMapping("/state/{state}")
    public ResponseEntity<Page<SpecialtyResponseDto>> getSpecialtiesByState(
            @PathVariable String state,
            @ParameterObject Pageable pageable) {
        log.info("Listing specialties with state={}, page={} size={}",
                state, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(specialtyService.getByState(state, pageable));
    }

    @Operation(summary = "Get a specialty by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialtyById(@PathVariable Long id) {
        log.info("Getting specialty with id={}", id);
        return ResponseEntity.ok(specialtyService.getById(id));
    }

    @Operation(summary = "Update a specialty")
    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody SpecialtyRequestDto requestDto) {
        log.info("Update specialty request for id={}: {}", id, requestDto);
        SpecialtyResponseDto response = specialtyService.updateSpecialty(id, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a specialty")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Long id) {
        log.info("Delete specialty request for id={}", id);
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }
}