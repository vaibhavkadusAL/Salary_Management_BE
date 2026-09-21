package com.acme.salarymanagement.exception;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with ID: " + id);
    }

    public EmployeeNotFoundException(String employeeCode) {
        super("Employee not found with code: " + employeeCode);
    }
}
