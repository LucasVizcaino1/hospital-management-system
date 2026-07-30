package com.hospital_management_system.demo.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private String lastname;
    @Enumerated(EnumType.STRING)
    private State state;
}
