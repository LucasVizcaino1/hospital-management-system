package com.hospital_management_system.demo.dto.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto {

    private String user;
    private String password;
    private PersonRequestDto person;
}
