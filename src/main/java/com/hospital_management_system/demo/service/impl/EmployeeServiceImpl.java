package com.hospital_management_system.demo.service.impl;


import com.hospital_management_system.demo.dto.request.EmployeeRequestDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.EmployeeMapper;
import com.hospital_management_system.demo.model.*;
import com.hospital_management_system.demo.repository.EmployeeRepository;
import com.hospital_management_system.demo.repository.PatientRepository;
import com.hospital_management_system.demo.repository.PersonRepository;
import com.hospital_management_system.demo.repository.UserRepository;
import com.hospital_management_system.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final PatientRepository patientRepository;
    private final PersonRepository personaRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;


    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        if (requestDto.getRol() == Rol.ADMIN) {
            throw new BusinessException(
                    "Cannot create ADMIN users through this endpoint. " +
                            "Only MEDIC role is allowed."
            );
        }

        Person person = personaRepository.findById(requestDto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Persona no encontrada con id: " + requestDto.getPersonId()));

        if (patientRepository.existsByPerson(person)) {
            throw new BusinessException(
                    "This person is already registered as a patient. " +
                            "A person cannot be both patient and employee."
            );
        }

        if (employeeRepository.findByPerson(person).isPresent()) {
            throw new BusinessException(
                    "This person is already an employee."
            );
        }

        if (userRepository.existsByUsername(person.getEmail())) {
            throw new BusinessException("Email already registered");
        }


        Employee employee = employeeMapper.toEntity(requestDto);
        employee.setPerson(person);


        User user = new User();
        user.setUsername(person.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setPerson(person);


        employee = employeeRepository.save(employee);
        userRepository.save(user);

        log.info("Employee created. id={}, rol={}, email={}",
                employee.getId(), employee.getRol(), person.getEmail());

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        if (dto.getRol() != null && dto.getRol() == Rol.ADMIN) {
            throw new BusinessException("Cannot assign ADMIN role through this endpoint.");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empleado no encontrado con id: " + id));

        if (dto.getPersonId() != null
                && !employee.getPerson().getId().equals(dto.getPersonId())) {
            throw new BusinessException(
                    "Cannot change the person associated with an employee. " +
                            "Delete and recreate if needed."
            );
        }


        employeeMapper.update(dto, employee);
        Employee updated = employeeRepository.save(employee);

        log.info("Employee updated. id={}", employee.getId());
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> allEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getEmployeeByState(String state, Pageable pageable) {
        Page<Employee> page = employeeRepository.findByState(Enum.valueOf(State.class, state.toUpperCase()), pageable);
        return page.map(employeeMapper::toResponse);
    }


    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found by id: " + id));

        employeeRepository.delete(employee);

        log.info("Employee deleted. id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeResponseDto> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toResponse);
    }

    @Override
    public Optional<EmployeeResponseDto> getAuthenticatedEmployee(String username) {
        return userRepository.findByUsername(username)
                .map(User::getPerson)
                .flatMap(employeeRepository::findByPerson)
                .map(employeeMapper::toResponse);
    }





}