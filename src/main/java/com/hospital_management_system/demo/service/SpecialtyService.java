package com.hospital_management_system.demo.service;

import com.hospital_management_system.demo.dto.request.SpecialtyRequestDto;
import com.hospital_management_system.demo.dto.response.SpecialtyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialtyService {
    SpecialtyResponseDto createSpecialty(SpecialtyRequestDto requestDto);

    SpecialtyResponseDto updateSpecialty(Long id, SpecialtyRequestDto requestDto);

    void deleteSpecialty(Long id);

    SpecialtyResponseDto getById(Long id);

    Page<SpecialtyResponseDto> getAllSpecialties(Pageable pageable);

    Page<SpecialtyResponseDto> searchByName(String name, Pageable pageable);

    Page<SpecialtyResponseDto> getByState(String state, Pageable pageable);

}
