package com.hospital_management_system.demo.dto.request;

import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDto {

    private Rol rol;


    private Long personId;
    private State state;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
