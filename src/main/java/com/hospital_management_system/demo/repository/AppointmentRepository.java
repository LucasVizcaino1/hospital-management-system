package com.hospital_management_system.demo.repository;

import com.hospital_management_system.demo.model.Appointment;
import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.Patient;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByPatient(Patient patient, Pageable pageable);

    Page<Appointment> findByEmployee(Employee employee, Pageable pageable);

    Page<Appointment> findByState(State state, Pageable pageable);

    Page<Appointment> findByDateBetween(LocalDateTime start, LocalDateTime fin, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.reason) LIKE LOWER(CONCAT('%', :reason, '%'))")
    Page<Appointment> searchByReason(@Param("reason") String reason, Pageable pageable);
}
