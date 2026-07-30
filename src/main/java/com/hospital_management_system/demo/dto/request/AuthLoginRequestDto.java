package com.hospital_management_system.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginRequestDto {

    private String user;
    private String password;
}
