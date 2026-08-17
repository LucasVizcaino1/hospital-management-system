package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.AuthLoginRequestDto;
import com.hospital_management_system.demo.dto.request.AuthRegisterRequestDto;
import com.hospital_management_system.demo.dto.response.AuthResponseDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.dto.response.UserResponseDto;
import com.hospital_management_system.demo.exception.BusinessException;
import com.hospital_management_system.demo.exception.InvalidCredentialsException;
import com.hospital_management_system.demo.exception.InvalidRequestException;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.PersonMapper;
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
    private final PersonMapper personMapper;


    @Override
    public AuthResponseDto login(AuthLoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for username={}", request.getEmail());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));
        Person person = user.getPerson();
        String rol;
        if (patientRepository.existsByPerson(person)) {
            rol = Rol.PATIENT.name();
        } else {
            Employee employee = employeeRepository.findByPerson(person)
                    .orElseThrow(() -> new BusinessException("Employee not found"));
            rol = employee.getRol().name();
        }

        String token = jwtUtil.generateToken(request.getEmail(), rol);
        log.info("Successful login for username={}, rol={}", request.getEmail(), rol);

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        response.setRol(String.valueOf(Rol.valueOf(rol)));
        response.setUsername(user.getUsername());
        return response;
    }


    @Override
    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }


        Person person = new Person();
        person.setName(request.getName());
        person.setLastname(request.getLastname());
        person.setEmail(request.getEmail());
        person.setState(State.ACTIVE);
        person = personRepository.save(person);

        Rol forcedRol = Rol.PATIENT;
        ensureRoleEntity(person, forcedRol);

        User user = new User();
        user.setUsername(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPerson(person);
        userRepository.save(user);

        log.info("User registered as PATIENT. username={}, personId={}",
                user.getUsername(), person.getId());

        String token = jwtUtil.generateToken(user.getUsername(), forcedRol.name());

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRol(forcedRol.name());
        return response;
    }


    @Override
    public UserResponseDto getAuthenticatedUser(String username) {
        log.info("Getting authenticated user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String rol = getRolForPerson(user.getPerson());

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUser(user.getUsername());
        dto.setPerson(personMapper.toResponse(user.getPerson()));
        dto.setRol(Rol.valueOf(rol));
        return dto;
    }


    private String getRolForPerson(Person person) {
        if (patientRepository.existsByPerson(person)) {
            return Rol.PATIENT.name();
        } else {
            Employee employee = employeeRepository.findByPerson(person)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
            return employee.getRol().name();
        }
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