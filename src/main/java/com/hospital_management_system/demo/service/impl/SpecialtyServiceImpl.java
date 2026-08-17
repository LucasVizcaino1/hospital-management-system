package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.SpecialtyRequestDto;
import com.hospital_management_system.demo.dto.response.SpecialtyResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.SpecialtyMapper;
import com.hospital_management_system.demo.model.Specialty;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.repository.SpecialtyRepository;
import com.hospital_management_system.demo.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyMapper specialtyMapper;

    @Override
    @Transactional
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto requestDto) {
        Specialty especialidad = specialtyMapper.toEntity(requestDto);
        especialidad = specialtyRepository.save(especialidad);

        log.info("Specialty created. id={}", especialidad.getId());
        return specialtyMapper.toResponse(especialidad);
    }

    @Override
    @Transactional
    public SpecialtyResponseDto updateSpecialty(Long id, SpecialtyRequestDto requestDto) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));


        specialtyMapper.update(requestDto, specialty);
        Specialty updateSpecialty = specialtyRepository.save(specialty);

        log.info("Specialty updated. id={}", specialty.getId());
        return specialtyMapper.toResponse(updateSpecialty);
    }

    @Override
    @Transactional
    public void deleteSpecialty(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with id: " + id));

        specialtyRepository.delete(specialty);

        log.info("Specialty deleted. id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getById(Long id) {
        return specialtyRepository.findById(id)
                .map(specialtyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException( "Specialty not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> getAllSpecialties(Pageable pageable) {
        return specialtyRepository.findAll(pageable)
                .map(specialtyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> searchByName(String name, Pageable pageable) {
        return specialtyRepository.searchByName(name, pageable)
                .map(specialtyMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> getByState(String stateStr, Pageable pageable) {
        State state;
        try {
            state = State.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid state: " + stateStr);
        }
        return specialtyRepository.findByState(state, pageable).map(specialtyMapper::toResponse);
    }



}