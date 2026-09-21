package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.EmployeeDto;
import com.acme.salarymanagement.dto.EmployeeUpdateRequest;
import com.acme.salarymanagement.dto.PageResponse;
import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.entity.SalaryRecord;
import com.acme.salarymanagement.exception.DuplicateEmployeeException;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.mapper.EmployeeMapper;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.specification.EmployeeSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeDto> getEmployees(
        int page,
        int size,
        String search,
        String country,
        String department,
        String designation,
        EmploymentStatus status,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        String sortField,
        String sortDirection
    ) {
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
            mapSortField(sortField)
        );

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Specification<Employee> spec = EmployeeSpecification.filterBy(
            search, country, department, designation, status, minSalary, maxSalary
        );

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

        List<EmployeeDto> dtoList = employeePage.getContent().stream()
            .map(employeeMapper::toDto)
            .collect(Collectors.toList());

        return new PageResponse<>(
            dtoList,
            employeePage.getNumber(),
            employeePage.getSize(),
            employeePage.getTotalElements(),
            employeePage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
        return employeeMapper.toDto(employee);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmployeeException("Employee with email '" + request.getEmail() + "' already exists");
        }

        String nextCode = generateNextEmployeeCode();

        Employee employee = new Employee(
            nextCode,
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getCountry(),
            request.getDepartment(),
            request.getDesignation(),
            request.getJoiningDate(),
            request.getStatus() != null ? request.getStatus() : EmploymentStatus.ACTIVE
        );

        SalaryRecord initialSalary = new SalaryRecord(
            employee,
            request.getBaseSalary(),
            request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO,
            request.getCurrency(),
            request.getEffectiveFrom(),
            request.getReason() != null ? request.getReason() : "Initial compensation"
        );

        employee.addSalaryRecord(initialSalary);

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        if (employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateEmployeeException("Email '" + request.getEmail() + "' is already in use by another employee");
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setCountry(request.getCountry());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setStatus(request.getStatus());

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    private synchronized String generateNextEmployeeCode() {
        String maxCode = employeeRepository.findMaxEmployeeCode();
        long nextVal = 1;
        if (maxCode != null && maxCode.startsWith("EMP-")) {
            try {
                nextVal = Long.parseLong(maxCode.substring(4)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("EMP-%05d", nextVal);
    }

    private String mapSortField(String sortField) {
        if (sortField == null || sortField.trim().isEmpty()) {
            return "id";
        }
        return switch (sortField.toLowerCase()) {
            case "name", "fullname", "firstname" -> "firstName";
            case "lastname" -> "lastName";
            case "email" -> "email";
            case "country" -> "country";
            case "department" -> "department";
            case "designation" -> "designation";
            case "status" -> "status";
            case "joiningdate" -> "joiningDate";
            case "updatedat" -> "updatedAt";
            case "createdat" -> "createdAt";
            default -> "id";
        };
    }
}
