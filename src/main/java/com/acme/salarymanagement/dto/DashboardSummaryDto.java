package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.repository.projection.CountryCountProjection;
import com.acme.salarymanagement.repository.projection.CurrencySalaryStatProjection;
import com.acme.salarymanagement.repository.projection.DepartmentCountProjection;
import com.acme.salarymanagement.repository.projection.DepartmentSalaryStatProjection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDto {

    private long totalEmployees;
    private long totalActiveEmployees;
    private long totalInactiveEmployees;
    private List<CurrencySalaryStatProjection> currencyStatistics;
    private List<CountryCountProjection> countryDistribution;
    private List<DepartmentCountProjection> departmentDistribution;
    private List<DepartmentSalaryStatProjection> departmentSalaryStatistics;
}
