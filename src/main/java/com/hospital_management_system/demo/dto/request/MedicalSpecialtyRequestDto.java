package com.hospital_management_system.demo.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalSpecialtyRequestDto {

    private Long employeeId;
    private Long specialtyId;
}
