package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.SpecialtyRequestDto;
import com.hospital_management_system.demo.dto.response.SpecialtyResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
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

    @Override
    @Transactional
    public SpecialtyResponseDto createSpecialty(SpecialtyRequestDto requestDto) {
        Specialty especialidad = toEntity(requestDto);
        especialidad = specialtyRepository.save(especialidad);

        log.info("Especialidad creada. id={}", especialidad.getId());
        return toResponse(especialidad);
    }

    @Override
    @Transactional
    public SpecialtyResponseDto updateSpecialty(Long id, SpecialtyRequestDto requestDto) {
        Specialty especialidad = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));

        updateEntity(especialidad, requestDto);
        Specialty updatEspecialidad = specialtyRepository.save(especialidad);

        log.info("Especialidad actualizada. id={}", especialidad.getId());
        return toResponse(updatEspecialidad);
    }

    @Override
    @Transactional
    public void deleteSpecialty(Long id) {
        Specialty especialidad = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));

        specialtyRepository.delete(especialidad);

        log.info("Especialidad eliminada. id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyResponseDto getById(Long id) {
        return specialtyRepository.findById(id)
                .map(this::toResponse)                           // ✅ era specialtyMapper::toResponse
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> getAllSpecialties(Pageable pageable) {
        return specialtyRepository.findAll(pageable)
                .map(this::toResponse);                          // ✅ era specialtyMapper::toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> searchByName(String nombre, Pageable pageable) {
        return specialtyRepository.searchByName(nombre, pageable)
                .map(this::toResponse);                          // ✅ era specialtyMapper::toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecialtyResponseDto> getByState(String stateStr, Pageable pageable) {
        State state;
        try {
            state = State.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + stateStr);
        }
        return specialtyRepository.findByState(state, pageable).map(this::toResponse);  // ✅ era specialtyMapper::toResponse
    }

    // ============================================
    // 🔧 MAPEO MANUAL (reemplaza a MapStruct)
    // ============================================

    private Specialty toEntity(SpecialtyRequestDto dto) {
        if (dto == null) return null;

        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());
        specialty.setState(dto.getState());
        return specialty;
    }

    private SpecialtyResponseDto toResponse(Specialty entity) {
        if (entity == null) return null;

        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setState(entity.getState());
        return dto;
    }

    private void updateEntity(Specialty specialty, SpecialtyRequestDto dto) {
        if (specialty == null || dto == null) return;

        if (dto.getName() != null)  specialty.setName(dto.getName());
        if (dto.getState() != null) specialty.setState(dto.getState());
    }
}