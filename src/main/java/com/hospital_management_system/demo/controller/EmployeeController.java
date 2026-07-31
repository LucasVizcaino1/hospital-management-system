package com.hospital_management_system.demo.controller;

import com.hospital_management_system.demo.dto.request.EmployeeRequestDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.service.EmployeeService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new employee")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(@Valid @RequestBody EmployeeRequestDto requestDto) {
        log.info("Create employee request received: {}", requestDto);
        EmployeeResponseDto response = employeeService.createEmployee(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "List all employees")
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        log.info("Listing all employees");
        List<EmployeeResponseDto> employees = employeeService.allEmployees();
        return ResponseEntity.ok(employees);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "Get an employee by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        log.info("Getting employee with id={}", id);
        Optional<EmployeeResponseDto> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "List employees by state (paginated)")
    @GetMapping("/status/{state}")
    public ResponseEntity<Page<EmployeeResponseDto>> getEmployeesByState(
            @PathVariable State state,
            @ParameterObject Pageable pageable) {
        log.info("Listing employees with state={}, page={} size={}",
                state, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(employeeService.getEmployeeByState(state.name(), pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an employee")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        log.info("Update employee request for id={}: {}", id, requestDto);
        EmployeeResponseDto response = employeeService.updateEmployee(id, requestDto);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Delete employee request for id={}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}