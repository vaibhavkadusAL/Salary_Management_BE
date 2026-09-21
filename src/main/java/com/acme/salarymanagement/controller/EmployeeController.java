package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.*;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.service.EmployeeService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Employees", description = "Employee management, search, and filtering APIs")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "List and search employees", description = "Retrieves paginated employee records with optional full-text search and multi-field filters.")
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeDto>> getEmployees(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) String designation,
        @RequestParam(required = false) EmploymentStatus status,
        @RequestParam(required = false) BigDecimal minSalary,
        @RequestParam(required = false) BigDecimal maxSalary,
        @RequestParam(required = false) String sort
    ) {
        String sortField = "id";
        String sortDirection = "asc";

        if (sort != null && !sort.trim().isEmpty()) {
            String[] parts = sort.split(",");
            sortField = parts[0].trim();
            if (parts.length > 1) {
                sortDirection = parts[1].trim();
            }
        }

        PageResponse<EmployeeDto> response = employeeService.getEmployees(
            page, size, search, country, department, designation, status, minSalary, maxSalary, sortField, sortDirection
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employee profile", description = "Retrieves complete employee profile including current compensation snapshot.")
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployee(@PathVariable Long employeeId) {
        EmployeeDto employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(ApiResponse.success(employee));
    }

    @Operation(summary = "Register new employee", description = "Creates a new employee record and establishes their initial salary.")
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeDto created = employeeService.createEmployee(request);
        return new ResponseEntity<>(ApiResponse.success(created, "Employee created successfully"), HttpStatus.CREATED);
    }

    @Operation(summary = "Update employee metadata", description = "Updates editable personal and employment metadata for an employee.")
    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(
        @PathVariable Long employeeId,
        @Valid @RequestBody EmployeeUpdateRequest request
    ) {
        EmployeeDto updated = employeeService.updateEmployee(employeeId, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Employee updated successfully"));
    }
}
