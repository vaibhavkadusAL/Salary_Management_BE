package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.entity.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public EmployeeDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public EmploymentStatus getStatus() {
        return status;
    }

    public void setStatus(EmploymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCurrentSalaryRecordId() {
        return currentSalaryRecordId;
    }

    public void setCurrentSalaryRecordId(Long currentSalaryRecordId) {
        this.currentSalaryRecordId = currentSalaryRecordId;
    }

    public BigDecimal getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(BigDecimal currentSalary) {
        this.currentSalary = currentSalary;
    }

    public BigDecimal getCurrentBonus() {
        return currentBonus;
    }

    public void setCurrentBonus(BigDecimal currentBonus) {
        this.currentBonus = currentBonus;
    }

    public BigDecimal getCurrentTotalCompensation() {
        return currentTotalCompensation;
    }

    public void setCurrentTotalCompensation(BigDecimal currentTotalCompensation) {
        this.currentTotalCompensation = currentTotalCompensation;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getSalaryEffectiveDate() {
        return salaryEffectiveDate;
    }

    public void setSalaryEffectiveDate(LocalDate salaryEffectiveDate) {
        this.salaryEffectiveDate = salaryEffectiveDate;
    }

    public String getSalaryChangeReason() {
        return salaryChangeReason;
    }

    public void setSalaryChangeReason(String salaryChangeReason) {
        this.salaryChangeReason = salaryChangeReason;
    }
}
