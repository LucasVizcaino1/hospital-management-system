package com.hospital_management_system.demo.repository;

import com.hospital_management_system.demo.model.Employee;
import com.hospital_management_system.demo.model.MedicalSpecialty;
import com.hospital_management_system.demo.model.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalSpecialtyRepository extends JpaRepository<MedicalSpecialty, Long> {

    Page<MedicalSpecialty> findByEmployee(Employee employee, Pageable pageable);

    Page<MedicalSpecialty> findBySpecialty(Specialty specialty, Pageable pageable);
}
