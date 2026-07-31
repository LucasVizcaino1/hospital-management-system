package com.hospital_management_system.demo.service;

import com.hospital_management_system.demo.dto.request.AuthLoginRequestDto;
import com.hospital_management_system.demo.dto.request.AuthRegisterRequestDto;
import com.hospital_management_system.demo.dto.response.AuthResponseDto;
import com.hospital_management_system.demo.dto.response.MessageResponseDto;

public interface AuthService {

    AuthResponseDto register(AuthRegisterRequestDto request);

    AuthResponseDto login(AuthLoginRequestDto request);
}
