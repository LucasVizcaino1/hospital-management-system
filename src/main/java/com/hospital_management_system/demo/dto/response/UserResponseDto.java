package com.hospital_management_system.demo.dto.response;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String user;
    private PersonResponseDto person;
    private Rol rol;
}
