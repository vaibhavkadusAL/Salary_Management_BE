package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.SalaryCreateRequest;
import com.acme.salarymanagement.dto.SalaryRecordDto;
import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.entity.SalaryRecord;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.InvalidSalaryException;
import com.acme.salarymanagement.mapper.SalaryMapper;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private SalaryMapper salaryMapper;

    @InjectMocks
    private SalaryService salaryService;

    private Employee employee;
    private SalaryRecord initialSalary;

    @BeforeEach
    void setUp() {
        employee = new Employee(
            "EMP-00100",
            "Alice",
            "Wong",
            "alice.wong@acme.org",
            "Singapore",
            "Finance",
            "Finance Director",
            LocalDate.of(2022, 5, 10),
            EmploymentStatus.ACTIVE
        );
        employee.setId(100L);

        initialSalary = new SalaryRecord(
            employee,
            new BigDecimal("150000.00"),
            new BigDecimal("20000.00"),
            "SGD",
            LocalDate.of(2022, 5, 10),
            "Initial compensation"
        );
        initialSalary.setId(201L);
    }

    @Test
    @DisplayName("Should add new salary revision without destroying previous history")
    void testAddSalaryRevision_Success() {
        SalaryCreateRequest request = new SalaryCreateRequest();
        request.setBaseSalary(new BigDecimal("165000.00"));
        request.setBonus(new BigDecimal("25000.00"));
        request.setCurrency("SGD");
        request.setEffectiveFrom(LocalDate.of(2024, 4, 1));
        request.setReason("Annual performance revision");

        when(employeeRepository.findById(100L)).thenReturn(Optional.of(employee));
        when(salaryRecordRepository.save(any(SalaryRecord.class))).thenAnswer(invocation -> {
            SalaryRecord r = invocation.getArgument(0);
            r.setId(202L);
            return r;
        });

        SalaryRecordDto result = salaryService.addSalaryRevision(100L, request);

        assertNotNull(result);
        assertEquals(202L, result.getId());
        assertEquals(new BigDecimal("165000.00"), result.getBaseSalary());
        assertEquals(new BigDecimal("25000.00"), result.getBonus());
        assertEquals(new BigDecimal("190000.00"), result.getTotalCompensation());
        assertEquals("SGD", result.getCurrency());
        verify(salaryRecordRepository, times(1)).save(any(SalaryRecord.class));
    }

    @Test
    @DisplayName("Should reject salary revision when base salary is negative")
    void testAddSalaryRevision_NegativeBase_ThrowsException() {
        SalaryCreateRequest request = new SalaryCreateRequest();
        request.setBaseSalary(new BigDecimal("-1000.00"));
        request.setBonus(new BigDecimal("5000.00"));

        when(employeeRepository.findById(100L)).thenReturn(Optional.of(employee));

        assertThrows(InvalidSalaryException.class, () -> salaryService.addSalaryRevision(100L, request));
        verify(salaryRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject salary revision when bonus is negative")
    void testAddSalaryRevision_NegativeBonus_ThrowsException() {
        SalaryCreateRequest request = new SalaryCreateRequest();
        request.setBaseSalary(new BigDecimal("100000.00"));
        request.setBonus(new BigDecimal("-500.00"));

        when(employeeRepository.findById(100L)).thenReturn(Optional.of(employee));

        assertThrows(InvalidSalaryException.class, () -> salaryService.addSalaryRevision(100L, request));
        verify(salaryRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when adding salary for non-existent employee")
    void testAddSalaryRevision_EmployeeNotFound_ThrowsException() {
        SalaryCreateRequest request = new SalaryCreateRequest();
        request.setBaseSalary(new BigDecimal("100000.00"));

        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> salaryService.addSalaryRevision(999L, request));
    }

    @Test
    @DisplayName("Should retrieve complete chronological salary history")
    void testGetSalaryHistory_Success() {
        SalaryRecord revisionSalary = new SalaryRecord(
            employee,
            new BigDecimal("165000.00"),
            new BigDecimal("25000.00"),
            "SGD",
            LocalDate.of(2024, 4, 1),
            "Annual promotion"
        );
        revisionSalary.setId(202L);

        when(employeeRepository.existsById(100L)).thenReturn(true);
        when(salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromDescCreatedAtDesc(100L))
            .thenReturn(List.of(revisionSalary, initialSalary));

        List<SalaryRecordDto> history = salaryService.getSalaryHistory(100L);

        assertNotNull(history);
        assertEquals(2, history.size());
        assertEquals(new BigDecimal("165000.00"), history.get(0).getBaseSalary());
        assertEquals(new BigDecimal("150000.00"), history.get(1).getBaseSalary());
    }
}
