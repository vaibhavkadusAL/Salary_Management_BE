package com.acme.salarymanagement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRecordDto {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal totalCompensation;
    private String currency;
    private LocalDate effectiveFrom;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
