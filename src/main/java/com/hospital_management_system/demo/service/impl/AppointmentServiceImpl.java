package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.AppointmentRequestDto;
import com.hospital_management_system.demo.dto.response.AppointmentResponseDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.AppointmentOverlapException;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.InvalidRequestException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.AppointmentMapper;
import com.hospital_management_system.demo.model.*;
import com.hospital_management_system.demo.repository.*;
import com.hospital_management_system.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final AppointmentMapper appointmentMapper;

    @Value("${app.appointment.duration-minutes:30}")
    private long durationMinutes;

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

        validateNoOverlap(employee.getId(), requestDto.getDateAttention(), null);

        Appointment appointment = appointmentMapper.toEntity(requestDto);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);

        appointment = appointmentRepository.save(appointment);

        log.info("Attention created. id={}", appointment.getId());
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attention not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByPatient(Long patientId, Pageable pageable) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + patientId));

        return appointmentRepository.findByPatient(patient, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByEmployee(Long employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));

        return appointmentRepository.findByEmployee(employee, pageable)
                .map(appointmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> getAppointmentsByStatus(State status, Pageable pageable) {
        return appointmentRepository.findByState(status, pageable)
                .map(appointmentMapper::toResponse);
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
                .map(appointmentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponseDto> searchByReason(String reason, Pageable pageable) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidRequestException("The search reason cannot be empty.");
        }

        return appointmentRepository.searchByReason(reason, pageable)
                .map(appointmentMapper::toResponse);
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

        if (requestDto.getDateAttention().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new InvalidRequestException("The attention date cannot be in the past");
        }

        appointmentMapper.update(requestDto, appointment);

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

        validateNoOverlap(appointment.getEmployee().getId(), appointment.getDate(), id);

        appointment = appointmentRepository.save(appointment);

        log.info("Attention updated. id={}", appointment.getId());

        return appointmentMapper.toResponse(appointment);
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
                .map(appointmentMapper::toResponse);
    }


    private void validateNoOverlap(Long employeeId, LocalDateTime start, Long excludeId) {
        LocalDateTime from = start.minusMinutes(durationMinutes);
        LocalDateTime to = start.plusMinutes(durationMinutes);

        boolean overlaps = (excludeId == null)
                ? appointmentRepository.existsByEmployeeIdAndStateNotAndDateAfterAndDateBefore(
                employeeId, State.CANCELLED, from, to)
                : appointmentRepository.existsByIdNotAndEmployeeIdAndStateNotAndDateAfterAndDateBefore(
                excludeId, employeeId, State.CANCELLED, from, to);

        if (overlaps) {
            throw new AppointmentOverlapException(String.format(
                    "The medic already has an appointment between %s and %s. " +
                            "Choose a different time or another medic.",
                    from, to));
        }
    }
}