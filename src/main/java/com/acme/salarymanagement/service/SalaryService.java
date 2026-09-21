package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.SalaryCreateRequest;
import com.acme.salarymanagement.dto.SalaryRecordDto;
import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.SalaryRecord;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.InvalidSalaryException;
import com.acme.salarymanagement.mapper.SalaryMapper;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryMapper salaryMapper;

    public SalaryService(
        SalaryRecordRepository salaryRecordRepository,
        EmployeeRepository employeeRepository,
        SalaryMapper salaryMapper
    ) {
        this.salaryRecordRepository = salaryRecordRepository;
        this.employeeRepository = employeeRepository;
        this.salaryMapper = salaryMapper;
    }

    @Transactional(readOnly = true)
    public List<SalaryRecordDto> getSalaryHistory(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        List<SalaryRecord> records = salaryRecordRepository
            .findByEmployeeIdOrderByEffectiveFromDescCreatedAtDesc(employeeId);

        return records.stream()
            .map(salaryMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public SalaryRecordDto addSalaryRevision(Long employeeId, SalaryCreateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (request.getBaseSalary() == null || request.getBaseSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSalaryException("Base salary must not be negative");
        }

        BigDecimal bonus = request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO;
        if (bonus.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSalaryException("Bonus must not be negative");
        }

        SalaryRecord newRecord = new SalaryRecord(
            employee,
            request.getBaseSalary(),
            bonus,
            request.getCurrency(),
            request.getEffectiveFrom(),
            request.getReason()
        );

        employee.addSalaryRecord(newRecord);
        SalaryRecord saved = salaryRecordRepository.save(newRecord);
        return salaryMapper.toDto(saved);
    }
}
