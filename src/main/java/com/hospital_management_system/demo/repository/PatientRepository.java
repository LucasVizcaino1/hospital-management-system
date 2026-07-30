package com.hospital_management_system.demo.repository;

import com.hospital_management_system.demo.model.Patient;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByState(State state);

    Page<Patient> findByState(State state, Pageable pageable);

    boolean existsByPerson(Person person);

    @Query("SELECT p FROM Patient p JOIN User u ON u.person = p.person WHERE u.username = :username")
    Optional<Patient> findByUserUsername(@Param("username") String username);
}