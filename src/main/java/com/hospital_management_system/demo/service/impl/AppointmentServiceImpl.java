package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.AppointmentRequestDto;
import com.hospital_management_system.demo.dto.response.AppointmentResponseDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.InvalidRequestException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.model.*;
import com.hospital_management_system.demo.repository.*;
import com.hospital_management_system.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto requestDto) {
        if (requestDto.getDateAttention() == null) {
            throw new InvalidRequestException("The attention date is required");
        }

        if (requestDto.getDateAttention().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new InvalidRequestException("The attention date cannot be in the past");
        }

        Patient patient = patientRepository.findById(requestDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + requestDto.getPatientId()));

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + requestDto.getEmployeeId()));

        Appointment appointment = toEntity(requestDto);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);

        appointment = appointmentRepository.save(appointment);

        log.info("Attention created. id={}", appointment.getId());
        return toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attention not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + patientId));

        return appointmentRepository.findByPatient(patient, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByEmployee(Long employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));

        return appointmentRepository.findByEmployee(employee, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByStatus(State status, Pageable pageable) {
        return appointmentRepository.findByState(status, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime startDate,
                                                                   LocalDateTime endDate,
                                                                   Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new InvalidRequestException("You must provide both start and end dates for the filter.");
        }

        if (endDate.isBefore(startDate)) {
            throw new InvalidRequestException("The end date cannot be before the start date.");
        }

        return appointmentRepository.findByDateBetween(startDate, endDate, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> searchByReason(String reason, Pageable pageable) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidRequestException("The search reason cannot be empty.");
        }

        return appointmentRepository.searchByReason(reason, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public AppointmentResponseDto updateAppointment(Long id, AppointmentRequestDto requestDto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attention not found with id: " + id));

        if (appointment.getState() == State.COMPLETED) {
            throw new BusinessException("Cannot update an attention that is already completed.");
        }

        if (requestDto.getDateAttention() == null) {
            throw new InvalidRequestException("The attention date is required");
        }

        appointment.setDate(requestDto.getDateAttention());
        appointment.setReason(requestDto.getReason());
        appointment.setState(requestDto.getState());

        if (requestDto.getPatientId() != null
                && !requestDto.getPatientId().equals(appointment.getPatient().getId())) {
            appointment.setPatient(patientRepository.findById(requestDto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient not found with id: " + requestDto.getPatientId())));
        }

        if (requestDto.getEmployeeId() != null
                && !requestDto.getEmployeeId().equals(appointment.getEmployee().getId())) {
            appointment.setEmployee(employeeRepository.findById(requestDto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + requestDto.getEmployeeId())));
        }

        appointment = appointmentRepository.save(appointment);

        log.info("Attention updated. id={}", appointment.getId());

        return toResponse(appointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attention not found with id: " + id);
        }
        appointmentRepository.deleteById(id);

        log.info("Attention deleted. id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAuthenticatedPatientAttentions(String username, Pageable pageable) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        Patient patient = patientRepository.findByPerson(user.getPerson())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient not found for the authenticated user: " + username));

        return appointmentRepository.findByPatient(patient, pageable)
                .map(this::toResponse);
    }


    private Appointment toEntity(AppointmentRequestDto dto) {
        if (dto == null) return null;

        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDateAttention());
        appointment.setReason(dto.getReason());
        appointment.setState(dto.getState());
        return appointment;
    }

    private AppointmentResponseDto toResponse(Appointment entity) {
        if (entity == null) return null;

        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(entity.getId());
        dto.setDateAttention(entity.getDate());
        dto.setState(entity.getState());
        dto.setPatient(toPatientResponse(entity.getPatient()));
        dto.setEmployee(toEmployeeResponse(entity.getEmployee()));
        return dto;
    }

    private PatientResponseDto toPatientResponse(Patient patient) {
        if (patient == null) return null;

        PatientResponseDto dto = new PatientResponseDto();
        dto.setId(patient.getId());
        dto.setState(patient.getState());
        dto.setRol(patient.getRol());
        dto.setPerson(toPersonResponse(patient.getPerson()));
        return dto;
    }

    private PersonResponseDto toPersonResponse(Person person) {
        if (person == null) return null;

        PersonResponseDto dto = new PersonResponseDto();
        dto.setId(person.getId());
        dto.setName(person.getName());
        dto.setLastname(person.getLastname());
        dto.setEmail(person.getEmail());
        dto.setState(person.getState());
        return dto;
    }

    private EmployeeResponseDto toEmployeeResponse(Employee employee) {
        if (employee == null) return null;

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(employee.getId());
        dto.setRol(employee.getRol());
        dto.setState(employee.getState());
        dto.setPersona(toPersonResponse(employee.getPerson()));
        return dto;
    }
}