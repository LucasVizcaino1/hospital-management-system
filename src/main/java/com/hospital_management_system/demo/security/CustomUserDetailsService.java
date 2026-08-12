package com.hospital_management_system.demo.security;

import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.User;
import com.hospital_management_system.demo.repository.EmployeeRepository;
import com.hospital_management_system.demo.repository.PatientRepository;
import com.hospital_management_system.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Person person = user.getPerson();
        Rol rol;
        Long principalId;

        var patientOpt = patientRepository.findByPerson(person);
        if (patientOpt.isPresent()) {
            rol = Rol.PATIENT;
            principalId = patientOpt.get().getId();
        } else {
            Employee employee = employeeRepository.findByPerson(person)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "No role (Patient/Employee) found for user: " + username));
            rol = employee.getRol();
            principalId = employee.getId();
        }

        return new HospitalUserDetails(
                user.getUsername(),
                user.getId(),
                principalId,
                rol
        );
    }
}