package com.hospital_management_system.demo.dto.response;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String user;
    private PersonResponseDto person;
}
