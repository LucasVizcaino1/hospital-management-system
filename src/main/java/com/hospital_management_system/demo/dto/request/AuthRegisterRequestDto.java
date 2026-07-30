package com.hospital_management_system.demo.dto.request;

import com.hospital_management_system.demo.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthRegisterRequestDto {
    private String user;
    private String password;
    private Rol rol;
    private Long personId;
}
