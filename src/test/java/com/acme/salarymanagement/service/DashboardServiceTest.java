package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.DashboardSummaryDto;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import com.acme.salarymanagement.repository.projection.CountryCountProjection;
import com.acme.salarymanagement.repository.projection.DepartmentCountProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should aggregate organizational metrics and distributions correctly")
    void testGetDashboardSummary() {
        when(employeeRepository.count()).thenReturn(10000L);
        when(employeeRepository.countByStatus(EmploymentStatus.ACTIVE)).thenReturn(9500L);
        when(employeeRepository.countByStatus(EmploymentStatus.INACTIVE)).thenReturn(500L);

        when(salaryRecordRepository.getCurrencySalaryStatistics()).thenReturn(Collections.emptyList());
        when(salaryRecordRepository.getDepartmentSalaryStatistics()).thenReturn(Collections.emptyList());

        CountryCountProjection countryProj = mock(CountryCountProjection.class);
        when(countryProj.getCountry()).thenReturn("United States");
        when(countryProj.getCount()).thenReturn(3500L);
        when(employeeRepository.countEmployeesByCountry()).thenReturn(List.of(countryProj));

        DepartmentCountProjection deptProj = mock(DepartmentCountProjection.class);
        when(deptProj.getDepartment()).thenReturn("Engineering");
        when(deptProj.getCount()).thenReturn(2800L);
        when(employeeRepository.countEmployeesByDepartment()).thenReturn(List.of(deptProj));

        DashboardSummaryDto summary = dashboardService.getDashboardSummary();

        assertNotNull(summary);
        assertEquals(10000L, summary.getTotalEmployees());
        assertEquals(9500L, summary.getTotalActiveEmployees());
        assertEquals(500L, summary.getTotalInactiveEmployees());
        assertEquals(1, summary.getCountryDistribution().size());
        assertEquals("United States", summary.getCountryDistribution().get(0).getCountry());
        assertEquals(1, summary.getDepartmentDistribution().size());
        assertEquals("Engineering", summary.getDepartmentDistribution().get(0).getDepartment());

        verify(employeeRepository, times(1)).count();
        verify(employeeRepository, times(1)).countByStatus(EmploymentStatus.ACTIVE);
        verify(employeeRepository, times(1)).countByStatus(EmploymentStatus.INACTIVE);
    }
}
