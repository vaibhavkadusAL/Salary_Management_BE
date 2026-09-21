package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.entity.EmploymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String country;
    private String department;
    private String designation;
    private LocalDate joiningDate;
    private EmploymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Current compensation snapshot
    private Long currentSalaryRecordId;
    private BigDecimal currentSalary;
    private BigDecimal currentBonus;
    private BigDecimal currentTotalCompensation;
    private String currency;
    private LocalDate salaryEffectiveDate;
    private String salaryChangeReason;
}
