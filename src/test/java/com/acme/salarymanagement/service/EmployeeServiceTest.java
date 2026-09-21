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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(
            "EMP-00001",
            "John",
            "Doe",
            "john.doe@acme.org",
            "United States",
            "Engineering",
            "Software Engineer",
            LocalDate.of(2023, 1, 15),
            EmploymentStatus.ACTIVE
        );
        employee.setId(1L);

        SalaryRecord salary = new SalaryRecord(
            employee,
            new BigDecimal("120000.00"),
            new BigDecimal("15000.00"),
            "USD",
            LocalDate.of(2023, 1, 15),
            "Initial setup"
        );
        salary.setId(10L);
        employee.addSalaryRecord(salary);
    }

    @Test
    @DisplayName("Should create employee successfully with initial salary")
    void testCreateEmployee_Success() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@acme.org");
        request.setCountry("United States");
        request.setDepartment("Finance");
        request.setDesignation("Financial Analyst");
        request.setJoiningDate(LocalDate.of(2024, 2, 1));
        request.setStatus(EmploymentStatus.ACTIVE);
        request.setBaseSalary(new BigDecimal("95000.00"));
        request.setBonus(new BigDecimal("8000.00"));
        request.setCurrency("USD");
        request.setEffectiveFrom(LocalDate.of(2024, 2, 1));

        when(employeeRepository.existsByEmail("jane.smith@acme.org")).thenReturn(false);
        when(employeeRepository.findMaxEmployeeCode()).thenReturn("EMP-00005");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            e.setId(6L);
            return e;
        });

        EmployeeDto result = employeeService.createEmployee(request);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("EMP-00006", result.getEmployeeCode());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should reject employee creation when email already exists")
    void testCreateEmployee_DuplicateEmail_ThrowsException() {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setEmail("john.doe@acme.org");

        when(employeeRepository.existsByEmail("john.doe@acme.org")).thenReturn(true);

        assertThrows(DuplicateEmployeeException.class, () -> employeeService.createEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve employee by ID successfully")
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EMP-00001", result.getEmployeeCode());
        assertEquals("John Doe", result.getFullName());
        assertEquals(new BigDecimal("120000.00"), result.getCurrentSalary());
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when ID does not exist")
    void testGetEmployeeById_NotFound_ThrowsException() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
    }

    @Test
    @DisplayName("Should update employee successfully")
    void testUpdateEmployee_Success() {
        EmployeeUpdateRequest updateReq = new EmployeeUpdateRequest();
        updateReq.setFirstName("John");
        updateReq.setLastName("Updated");
        updateReq.setEmail("john.updated@acme.org");
        updateReq.setCountry("United States");
        updateReq.setDepartment("Engineering");
        updateReq.setDesignation("Lead Engineer");
        updateReq.setStatus(EmploymentStatus.ACTIVE);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("john.updated@acme.org", 1L)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.updateEmployee(1L, updateReq);

        assertNotNull(result);
        assertEquals("Updated", result.getLastName());
        assertEquals("Lead Engineer", result.getDesignation());
    }

    @Test
    @DisplayName("Should retrieve paginated employees with search/filter criteria")
    void testGetEmployees_Paginated() {
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<EmployeeDto> result = employeeService.getEmployees(
            0, 20, "John", "United States", "Engineering", null, EmploymentStatus.ACTIVE, null, null, "id", "asc"
        );

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        assertEquals(1, result.getTotalElements());
        assertEquals("EMP-00001", result.getData().get(0).getEmployeeCode());
    }
}
