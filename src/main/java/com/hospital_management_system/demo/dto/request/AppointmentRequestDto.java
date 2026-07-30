package com.hospital_management_system.demo.dto.request;


import com.hospital_management_system.demo.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppointmentRequestDto {
    private LocalDateTime time;
    private String reason;
    private Long patientId;
    private Long employeeId;
    private State state;
    private LocalDateTime dateAttention;
}
