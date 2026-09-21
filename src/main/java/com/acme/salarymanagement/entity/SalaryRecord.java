package com.acme.salarymanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "salary_records",
    indexes = {
        @Index(name = "idx_salary_emp_id", columnList = "employee_id"),
        @Index(name = "idx_salary_emp_effective", columnList = "employee_id, effective_from DESC"),
        @Index(name = "idx_salary_currency", columnList = "currency"),
        @Index(name = "idx_salary_effective_from", columnList = "effective_from")
    }
)
public class SalaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private Employee employee;

    @Column(name = "base_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "bonus", nullable = false, precision = 15, scale = 2)
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SalaryRecord(Employee employee, BigDecimal baseSalary, BigDecimal bonus,
                        String currency, LocalDate effectiveFrom, String reason) {
        this.employee = employee;
        this.baseSalary = baseSalary;
        this.bonus = bonus != null ? bonus : BigDecimal.ZERO;
        this.currency = currency;
        this.effectiveFrom = effectiveFrom;
        this.reason = reason;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.bonus == null) {
            this.bonus = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getTotalCompensation() {
        if (baseSalary == null) return BigDecimal.ZERO;
        return baseSalary.add(bonus != null ? bonus : BigDecimal.ZERO);
    }
}
