package com.acme.salarymanagement.exception;

public class SalaryRecordNotFoundException extends ResourceNotFoundException {
    public SalaryRecordNotFoundException(Long id) {
        super("Salary record not found with ID: " + id);
    }
}
