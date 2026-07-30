package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.PatientRequestDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.model.Patient;
import com.hospital_management_system.demo.model.Person;
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
    // ❌ ELIMINADO: private final PatientMapper patientMapper;

    @Override
    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto dto) {
        Person person = personRepository.findById(dto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + dto.getPersonId()));

        Patient patient = toEntity(dto);                    // ✅ era patientMapper.toEntity
        patient.setPerson(person);
        patient = patientRepository.save(patient);

        log.info("Patient created. id={}", patient.getId());
        return toResponse(patient);                          // ✅ era patientMapper.toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        log.info("Getting patient with id={}", id);
        return patientRepository.findById(id)
                .map(this::toResponse)                       // ✅ era patientMapper::toResponse
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    @Transactional
    public PatientResponseDto updatePatient(Long id, PatientRequestDto dto) {
        log.info("Updating patient with id={}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        updateEntity(patient, dto);                          // ✅ era patientMapper.updateEntity

        if (dto.getPersonId() != null && !dto.getPersonId().equals(patient.getPerson().getId())) {
            Person person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + dto.getPersonId()));
            patient.setPerson(person);   // después del updateEntity, para que gane el objeto completo
        }

        Patient updated = patientRepository.save(patient);
        log.info("Patient updated. id={}", updated.getId());
        return toResponse(updated);                          // ✅ era patientMapper.toResponse
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
        return patientRepository.findAll(pageable).map(this::toResponse);   // ✅ era patientMapper::toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDto> getAllPatientsByState(State state, Pageable pageable) {
        log.info("Listing patients by state={}", state);
        return patientRepository.findByState(state, pageable).map(this::toResponse);  // ✅ era patientMapper::toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> listAssets() {
        log.info("Listing all active patients");
        return patientRepository.findByState(State.ACTIVE).stream()
                .map(this::toResponse)                       // ✅ era patientMapper::toResponse
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getIdByUsername(String username) {
        log.info("Getting patient id for user: {}", username);
        Patient patient = patientRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user: " + username));
        return patient.getId();
    }

    private Patient toEntity(PatientRequestDto dto) {
        if (dto == null) return null;

        Patient patient = new Patient();
        patient.setRol(dto.getRol());
        patient.setState(dto.getState());
        return patient;
    }

    private PatientResponseDto toResponse(Patient entity) {
        if (entity == null) return null;

        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(entity.getId());
        dto.setRol(entity.getRol());
        dto.setState(entity.getState());
        dto.setPerson(toPersonResponse(entity.getPerson()));
        return dto;
    }

    private void updateEntity(Patient patient, PatientRequestDto dto) {
        if (patient == null || dto == null) return;

        if (dto.getRol() != null)   patient.setRol(dto.getRol());
        if (dto.getState() != null) patient.setState(dto.getState());
        // person ya lo actualiza updatePatient() después de llamar a esto.
    }

    private PersonResponseDto toPersonResponse(Person person) {
        if (person == null) return null;

        PersonResponseDto p = new PersonResponseDto();
        p.setId(person.getId());
        p.setName(person.getName());
        p.setLastname(person.getLastname());
        p.setEmail(person.getEmail());
        p.setState(person.getState());
        return p;
    }
}