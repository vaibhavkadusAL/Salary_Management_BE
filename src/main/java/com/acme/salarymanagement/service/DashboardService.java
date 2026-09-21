package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.DashboardSummaryDto;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        long total = employeeRepository.count();
        long active = employeeRepository.countByStatus(EmploymentStatus.ACTIVE);
        long inactive = employeeRepository.countByStatus(EmploymentStatus.INACTIVE);

        summary.setTotalEmployees(total);
        summary.setTotalActiveEmployees(active);
        summary.setTotalInactiveEmployees(inactive);

        summary.setCurrencyStatistics(salaryRecordRepository.getCurrencySalaryStatistics());
        summary.setCountryDistribution(employeeRepository.countEmployeesByCountry());
        summary.setDepartmentDistribution(employeeRepository.countEmployeesByDepartment());
        summary.setDepartmentSalaryStatistics(salaryRecordRepository.getDepartmentSalaryStatistics());

        return summary;
    }
}
