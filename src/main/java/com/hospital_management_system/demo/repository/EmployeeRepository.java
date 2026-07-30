package com.hospital_management_system.demo.repository;

import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByState(State state, Pageable pageable);

    Optional<Employee> findByPerson(Person person);

}
