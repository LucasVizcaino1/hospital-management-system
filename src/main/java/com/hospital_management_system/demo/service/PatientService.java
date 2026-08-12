package com.hospital_management_system.demo.service;

import com.hospital_management_system.demo.dto.request.PatientRequestDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {
    PatientResponseDto createPatient(PatientRequestDto requestDto);

    PatientResponseDto getPatientById(Long id);

    PatientResponseDto updatePatient(Long id, PatientRequestDto requestDto);

    void deletePatient(Long id);

    Page<PatientResponseDto> getAllPatients(Pageable pageable);

    Page<PatientResponseDto> getAllPatientsByState(State state, Pageable pageable);

    List<PatientResponseDto> listAssets();

}
