package com.hospital_management_system.demo.dto.request;

import com.hospital_management_system.demo.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialtyRequestDto {

    private String name;
    private State state;
}
