package com.hospital_management_system.demo.service;

import com.hospital_management_system.demo.dto.request.EmployeeRequestDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto);

    List<EmployeeResponseDto> allEmployees();

    Page<EmployeeResponseDto> getEmployeeByState(String State, Pageable pageable);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto);

    void deleteEmployee(Long id);

    Optional<EmployeeResponseDto> getEmployeeById(Long id);

    Optional<EmployeeResponseDto> getAuthenticatedEmployee(String username);

}
