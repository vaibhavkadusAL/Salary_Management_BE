package com.acme.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface DepartmentSalaryStatProjection {
    String getDepartment();
    String getCurrency();
    Long getEmployeeCount();
    BigDecimal getAverageBaseSalary();
    BigDecimal getAverageTotalCompensation();
}
