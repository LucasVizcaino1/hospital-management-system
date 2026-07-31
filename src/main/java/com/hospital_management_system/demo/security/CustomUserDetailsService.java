package com.hospital_management_system.demo.security;

import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.Rol;
import com.hospital_management_system.demo.model.User;
import com.hospital_management_system.demo.repository.EmployeeRepository;
import com.hospital_management_system.demo.repository.PatientRepository;
import com.hospital_management_system.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Person person = user.getPerson();
        Rol rol ;
        if(patientRepository.existsByPerson(person)){
            rol = Rol.PATIENT;
        }else {
            Employee employee = employeeRepository.findByPerson(person)
                    .orElseThrow(() -> new UsernameNotFoundException("Employee Not Found"));
            rol = employee.getRol();
        }

        List<SimpleGrantedAuthority> authorityList = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + rol.name())
        );

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorityList);
    }
}
