package com.hospital_management_system.demo.dto.response;


import com.hospital_management_system.demo.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {


    private Long id;
    private PatientResponseDto patient;
    private EmployeeResponseDto employee;
    private State state;
    private LocalDateTime dateAttention;
}
