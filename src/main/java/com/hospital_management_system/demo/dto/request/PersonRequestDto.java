package com.hospital_management_system.demo.dto.request;

import com.hospital_management_system.demo.model.State;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequestDto {

    private String name;
    private String lastname;
    private String email;
    private State state;
}
