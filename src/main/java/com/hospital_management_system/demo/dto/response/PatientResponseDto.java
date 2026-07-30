package com.hospital_management_system.demo.dto.response;


import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDto {
    private Long id;
    private Rol rol;
    private State state;
    private PersonResponseDto person;


}
