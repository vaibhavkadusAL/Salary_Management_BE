package com.acme.salarymanagement.repository.projection;

import java.math.BigDecimal;

public interface CurrencySalaryStatProjection {
    String getCurrency();
    Long getEmployeeCount();
    BigDecimal getTotalPayroll();
    BigDecimal getAverageSalary();
    BigDecimal getMinSalary();
    BigDecimal getMaxSalary();
}
