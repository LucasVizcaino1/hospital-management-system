package com.hospital_management_system.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalSpecialtyResponseDto {
    private Long id;
    private EmployeeResponseDto employeeId;
    private SpecialtyResponseDto specialtyId;
}
