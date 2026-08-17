package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.PatientRequestDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.PatientMapper;
import com.hospital_management_system.demo.model.Patient;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.repository.PatientRepository;
import com.hospital_management_system.demo.repository.PersonRepository;
import com.hospital_management_system.demo.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PersonRepository personRepository;
    private final PatientMapper patientMapper;


    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto dto) {
        Person person = personRepository.findById(dto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + dto.getPersonId()));

        Patient patient = new Patient();

        patient.setRol(Rol.PATIENT);
        patient.setState(dto.getState());
        patient.setPerson(person);

        patient = patientRepository.save(patient);

        log.info("Patient created. id={}, rol=PATIENT (forced)", patient.getId());
        return patientMapper.toResponse(patient);
    }


    @Override
    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto dto) {
        log.info("Updating patient with id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));


        if (dto.getPersonId() != null && !dto.getPersonId().equals(patient.getPerson().getId())) {
            Person person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person not found with id: " + dto.getPersonId()));
            patient.setPerson(person);
        }

        patientMapper.update(dto, patient);
        Patient updated = patientRepository.save(patient);
        log.info("Patient updated. id={}", updated.getId());
        return patientMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        log.info("Getting patient with id={}", id);
        return patientRepository.findById(id)
                .map(patientMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }


    @Override
    @Transactional
    public void deletePatient(Long id) {
        log.info("Deleting patient. id={}", id);
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatients(Pageable pageable) {
        log.info("Listing patients page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatientsByState(State state, Pageable pageable) {
        log.info("Listing patients by state={}", state);
        return patientRepository.findByState(state, pageable).map(patientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> listAssets() {
        log.info("Listing all active patients");
        return patientRepository.findByState(State.ACTIVE).stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }

}