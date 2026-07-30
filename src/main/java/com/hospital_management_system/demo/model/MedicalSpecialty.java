package com.hospital_management_system.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MedicalSpecialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "FK_medical_specialty"))
    private Employee employee;
    @ManyToOne
    private Specialty specialty;

}
