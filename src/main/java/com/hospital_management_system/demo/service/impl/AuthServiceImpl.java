package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.AuthLoginRequestDto;
import com.hospital_management_system.demo.dto.request.AuthRegisterRequestDto;
import com.hospital_management_system.demo.dto.response.AuthResponseDto;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.InvalidCredentialsException;
import com.hospital_management_system.demo.exception.InvalidRequestException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.Patient;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.model.User;
import com.hospital_management_system.demo.repository.EmployeeRepository;
import com.hospital_management_system.demo.repository.PatientRepository;
import com.hospital_management_system.demo.repository.PersonRepository;
import com.hospital_management_system.demo.repository.UserRepository;
import com.hospital_management_system.demo.security.JwtUtil;
import com.hospital_management_system.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;


    @Override
    public AuthResponseDto login(AuthLoginRequestDto request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username={}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(request.getUsername());
        log.info("Successful login for username={}", request.getUsername());


        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        return response;
    }


    @Override
    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new InvalidRequestException("Password is required");
        }
        if (request.getRol() == null) {
            throw new InvalidRequestException("Role is required");
        }
        if (request.getPersonId() == null) {
            throw new InvalidRequestException("Person id is required");
        }


        if (userRepository.existsByUsername(request.getName())) {
            throw new BusinessException("Username already in use: " + request.getName());
        }

        Person person = personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.getPersonId()));

        ensureRoleEntity(person, request.getRol());


        User user = new User();
        user.setUsername(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPerson(person);
        userRepository.save(user);

        log.info("User registered. username={}, personId={}", user.getUsername(), person.getId());


        String token = jwtUtil.generateToken(user.getUsername());
        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        return response;
    }


    private void ensureRoleEntity(Person person, Rol rol) {
        boolean alreadyPatient = patientRepository.existsByPerson(person);
        boolean alreadyEmployee = employeeRepository.findByPerson(person).isPresent();
        if (alreadyPatient || alreadyEmployee) {
            return;
        }

        if (rol == Rol.PATIENT) {
            Patient patient = new Patient();
            patient.setPerson(person);
            patient.setRol(Rol.PATIENT);
            patient.setState(State.ACTIVE);
            patientRepository.save(patient);
        } else {
            Employee employee = new Employee();
            employee.setPerson(person);
            employee.setRol(rol);
            employee.setState(State.ACTIVE);
            employeeRepository.save(employee);
        }
    }
}