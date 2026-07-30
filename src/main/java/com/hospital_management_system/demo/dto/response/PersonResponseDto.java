package com.hospital_management_system.demo.dto.response;

import com.hospital_management_system.demo.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDto {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private State state;
}
