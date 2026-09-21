package com.acme.salarymanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(
    name = "employees",
    indexes = {
        @Index(name = "idx_emp_code", columnList = "employee_code", unique = true),
        @Index(name = "idx_emp_email", columnList = "email", unique = true),
        @Index(name = "idx_emp_country", columnList = "country"),
        @Index(name = "idx_emp_department", columnList = "department"),
        @Index(name = "idx_emp_designation", columnList = "designation"),
        @Index(name = "idx_emp_status", columnList = "status"),
        @Index(name = "idx_emp_country_dept", columnList = "country, department")
    }
)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 32)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("effectiveFrom DESC, createdAt DESC")
    private List<SalaryRecord> salaryRecords = new ArrayList<>();

    public Employee() {}

    public Employee(String employeeCode, String firstName, String lastName, String email,
                    String country, String department, String designation,
                    LocalDate joiningDate, EmploymentStatus status) {
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.department = department;
        this.designation = designation;
        this.joiningDate = joiningDate;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addSalaryRecord(SalaryRecord salaryRecord) {
        salaryRecords.add(salaryRecord);
        salaryRecord.setEmployee(this);
    }

    public SalaryRecord getCurrentSalaryRecord() {
        if (salaryRecords == null || salaryRecords.isEmpty()) {
            return null;
        }
        return salaryRecords.stream()
            .max(Comparator.comparing(SalaryRecord::getEffectiveFrom)
                .thenComparing(SalaryRecord::getId, Comparator.nullsLast(Comparator.naturalOrder())))
            .orElse(salaryRecords.get(0));
    }

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
        return firstName + " " + lastName;
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

    public List<SalaryRecord> getSalaryRecords() {
        return salaryRecords;
    }

    public void setSalaryRecords(List<SalaryRecord> salaryRecords) {
        this.salaryRecords = salaryRecords;
    }
}
