package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.repository.projection.CountryCountProjection;
import com.acme.salarymanagement.repository.projection.CurrencySalaryStatProjection;
import com.acme.salarymanagement.repository.projection.DepartmentCountProjection;
import com.acme.salarymanagement.repository.projection.DepartmentSalaryStatProjection;

import java.util.List;

public class DashboardSummaryDto {

    private long totalEmployees;
    private long totalActiveEmployees;
    private long totalInactiveEmployees;
    private List<CurrencySalaryStatProjection> currencyStatistics;
    private List<CountryCountProjection> countryDistribution;
    private List<DepartmentCountProjection> departmentDistribution;
    private List<DepartmentSalaryStatProjection> departmentSalaryStatistics;

    public DashboardSummaryDto() {}

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalActiveEmployees() {
        return totalActiveEmployees;
    }

    public void setTotalActiveEmployees(long totalActiveEmployees) {
        this.totalActiveEmployees = totalActiveEmployees;
    }

    public long getTotalInactiveEmployees() {
        return totalInactiveEmployees;
    }

    public void setTotalInactiveEmployees(long totalInactiveEmployees) {
        this.totalInactiveEmployees = totalInactiveEmployees;
    }

    public List<CurrencySalaryStatProjection> getCurrencyStatistics() {
        return currencyStatistics;
    }

    public void setCurrencyStatistics(List<CurrencySalaryStatProjection> currencyStatistics) {
        this.currencyStatistics = currencyStatistics;
    }

    public List<CountryCountProjection> getCountryDistribution() {
        return countryDistribution;
    }

    public void setCountryDistribution(List<CountryCountProjection> countryDistribution) {
        this.countryDistribution = countryDistribution;
    }

    public List<DepartmentCountProjection> getDepartmentDistribution() {
        return departmentDistribution;
    }

    public void setDepartmentDistribution(List<DepartmentCountProjection> departmentDistribution) {
        this.departmentDistribution = departmentDistribution;
    }

    public List<DepartmentSalaryStatProjection> getDepartmentSalaryStatistics() {
        return departmentSalaryStatistics;
    }

    public void setDepartmentSalaryStatistics(List<DepartmentSalaryStatProjection> departmentSalaryStatistics) {
        this.departmentSalaryStatistics = departmentSalaryStatistics;
    }
}
