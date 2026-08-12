package com.hospital_management_system.demo.dto.request;


import com.hospital_management_system.demo.model.State;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppointmentRequestDto {

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Size(max = 500)
    private String reason;

    @NotNull
    private Long patientId;

    @NotNull
    private Long employeeId;

    @NotNull
    private State state;

    @NotNull
    private LocalDateTime dateAttention;
}
