package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.ApiResponse;
import com.acme.salarymanagement.dto.SalaryCreateRequest;
import com.acme.salarymanagement.dto.SalaryRecordDto;
import com.acme.salarymanagement.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Salary Management", description = "Salary revision and chronological audit history APIs")
@RestController
@RequestMapping("/api/employees/{employeeId}/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @Operation(summary = "Get employee salary history", description = "Retrieves all past and current salary records chronologically for an employee.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryRecordDto>>> getSalaryHistory(@PathVariable Long employeeId) {
        List<SalaryRecordDto> history = salaryService.getSalaryHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @Operation(summary = "Record salary revision", description = "Appends a new salary record to the employee's chronological compensation history without destroying previous records.")
    @PostMapping
    public ResponseEntity<ApiResponse<SalaryRecordDto>> addSalary(
        @PathVariable Long employeeId,
        @Valid @RequestBody SalaryCreateRequest request
    ) {
        SalaryRecordDto created = salaryService.addSalaryRevision(employeeId, request);
        return new ResponseEntity<>(ApiResponse.success(created, "Salary revision recorded successfully"), HttpStatus.CREATED);
    }
}
