package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.EmployeeRequestDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.model.*;
import com.hospital_management_system.demo.repository.EmployeeRepository;
import com.hospital_management_system.demo.repository.PersonRepository;
import com.hospital_management_system.demo.repository.UserRepository;
import com.hospital_management_system.demo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PersonRepository personaRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        // 🔒 SEGURIDAD: validar que no se pueda crear ADMIN vía API
        if (requestDto.getRol() == Rol.ADMIN) {
            throw new BusinessException(
                    "Cannot create ADMIN users through this endpoint. " +
                            "Only MEDIC role is allowed."
            );
        }

        Person person = personaRepository.findById(requestDto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Persona no encontrada con id: " + requestDto.getPersonId()));

        Employee employee = new Employee();
        employee.setRol(requestDto.getRol());  // ✅ Rol elegido por el admin
        employee.setState(requestDto.getState());
        employee.setPerson(person);

        employee = employeeRepository.save(employee);

        log.info("Employee created. id={}, rol={}", employee.getId(), employee.getRol());
        return toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        // 🔒 SEGURIDAD: validar también en update
        if (dto.getRol() != null && dto.getRol() == Rol.ADMIN) {
            throw new BusinessException("Cannot assign ADMIN role through this endpoint.");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empleado no encontrado con id: " + id));

        if (!employee.getPerson().getId().equals(dto.getPersonId())) {
            Person persona = personaRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Persona no encontrada con id: " + dto.getPersonId()));
            employee.setPerson(persona);
        }

        // Solo actualizamos los campos permitidos
        if (dto.getRol() != null) employee.setRol(dto.getRol());
        if (dto.getState() != null) employee.setState(dto.getState());

        Employee updated = employeeRepository.save(employee);

        log.info("Employee updated. id={}", employee.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> allEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDto> getEmployeeByState(String estado, Pageable pageable) {
        Page<Employee> page = employeeRepository.findByState(Enum.valueOf(State.class, estado.toUpperCase()), pageable);
        return page.map(this::toResponse);
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
                .map(this::toResponse);
    }

    @Override
    public Optional<EmployeeResponseDto> getAuthenticatedEmployee(String username) {
        return userRepository.findByUsername(username)
                .map(User::getPerson)
                .flatMap(employeeRepository::findByPerson)
                .map(this::toResponse);
    }


    private Employee toEntity(EmployeeRequestDto dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setRol(dto.getRol());
        employee.setState(dto.getState());
        return employee;
    }

    private EmployeeResponseDto toResponse(Employee entity) {
        if (entity == null) return null;

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setId(entity.getId());
        dto.setRol(entity.getRol());
        dto.setState(entity.getState());
        dto.setPersona(toPersonResponse(entity.getPerson()));
        return dto;
    }

    private void updateEntity(Employee employee, EmployeeRequestDto dto) {
        if (employee == null || dto == null) return;

        if (dto.getRol() != null)   employee.setRol(dto.getRol());
        if (dto.getState() != null) employee.setState(dto.getState());
    }

    private PersonResponseDto toPersonResponse(Person person) {
        if (person == null) return null;

        PersonResponseDto response = new PersonResponseDto();
        response.setId(person.getId());
        response.setName(person.getName());
        response.setLastname(person.getLastname());
        response.setEmail(person.getEmail());
        response.setState(person.getState());

        return response;
    }
}