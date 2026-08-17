package com.hospital_management_system.demo.exception;

public class AppointmentOverlapException extends RuntimeException {
    public AppointmentOverlapException(String message) {
        super(message);
    }
}
