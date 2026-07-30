package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.AuthLoginRequestDto;
import com.hospital_management_system.demo.dto.request.AuthRegisterRequestDto;
import com.hospital_management_system.demo.dto.response.AuthResponseDto;
import com.hospital_management_system.demo.dto.response.MessageResponseDto;
import com.hospital_management_system.demo.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public MessageResponseDto register(AuthRegisterRequestDto request) {
        return null;
    }

    @Override
    public AuthResponseDto login(AuthLoginRequestDto request) {
        return null;
    }

}
