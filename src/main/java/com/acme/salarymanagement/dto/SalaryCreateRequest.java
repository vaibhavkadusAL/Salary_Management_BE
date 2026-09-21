package com.acme.salarymanagement.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SalaryCreateRequest {

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be positive or zero")
    private BigDecimal baseSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Bonus must be positive or zero")
    private BigDecimal bonus = BigDecimal.ZERO;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^(USD|INR|GBP|EUR|CAD|AUD|SGD)$", message = "Currency must be one of: USD, INR, GBP, EUR, CAD, AUD, SGD")
    private String currency;

    @NotNull(message = "Salary effective date is required")
    private LocalDate effectiveFrom;

    @NotBlank(message = "Salary change reason is required")
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    public SalaryCreateRequest() {}

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
