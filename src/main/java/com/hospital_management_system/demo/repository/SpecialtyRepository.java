package com.hospital_management_system.demo.repository;

import com.hospital_management_system.demo.model.Specialty;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    Page<Specialty> findByState(State state, Pageable pageable);

    @Query("SELECT e FROM Specialty e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Specialty> searchByName(@Param("name") String name, Pageable pageable);
}
