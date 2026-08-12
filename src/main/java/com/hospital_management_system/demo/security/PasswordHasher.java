package com.hospital_management_system.demo.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = args.length > 0 ? args[0] : "Admin123!";
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("Password: " + rawPassword);
        System.out.println("Hash BCrypt (usar en SQL):");
        System.out.println(hashedPassword);
    }
}