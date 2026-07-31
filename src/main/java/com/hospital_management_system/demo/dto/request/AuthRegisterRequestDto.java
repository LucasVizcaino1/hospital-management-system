package com.hospital_management_system.demo.dto.request;

import com.hospital_management_system.demo.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterRequestDto {
    private String username;
    private String Lastname;
    private String password;
    private String email;
    private String name;
    private Rol rol;
}
