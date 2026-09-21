package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.entity.EmploymentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @NotNull(message = "Joining date is required")
    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;

    @NotNull(message = "Employment status is required")
    @Builder.Default
    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    // Initial compensation fields
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be positive or zero")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bonus must be positive or zero")
    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^(USD|INR|GBP|EUR|CAD|AUD|SGD)$", message = "Currency must be one of: USD, INR, GBP, EUR, CAD, AUD, SGD")
    private String currency;

    @NotNull(message = "Salary effective date is required")
    private LocalDate effectiveFrom;

    @Builder.Default
    private String reason = "Initial compensation";
}
