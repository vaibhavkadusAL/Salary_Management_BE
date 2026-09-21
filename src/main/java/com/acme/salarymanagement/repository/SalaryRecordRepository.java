package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.entity.SalaryRecord;
import com.acme.salarymanagement.repository.projection.CurrencySalaryStatProjection;
import com.acme.salarymanagement.repository.projection.DepartmentSalaryStatProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

    List<SalaryRecord> findByEmployeeIdOrderByEffectiveFromDescCreatedAtDesc(Long employeeId);

    Optional<SalaryRecord> findTopByEmployeeIdOrderByEffectiveFromDescCreatedAtDesc(Long employeeId);

    @Query(value = """
        SELECT 
            s.currency AS currency,
            COUNT(s.id) AS employeeCount,
            SUM(s.base_salary + s.bonus) AS totalPayroll,
            AVG(s.base_salary + s.bonus) AS averageSalary,
            MIN(s.base_salary + s.bonus) AS minSalary,
            MAX(s.base_salary + s.bonus) AS maxSalary
        FROM salary_records s
        INNER JOIN (
            SELECT employee_id, MAX(effective_from) AS max_eff
            FROM salary_records
            GROUP BY employee_id
        ) latest ON s.employee_id = latest.employee_id AND s.effective_from = latest.max_eff
        INNER JOIN employees e ON e.id = s.employee_id
        WHERE e.status = 'ACTIVE'
        GROUP BY s.currency
        ORDER BY employeeCount DESC
        """, nativeQuery = true)
    List<CurrencySalaryStatProjection> getCurrencySalaryStatistics();

    @Query(value = """
        SELECT 
            e.department AS department,
            s.currency AS currency,
            COUNT(s.id) AS employeeCount,
            AVG(s.base_salary) AS averageBaseSalary,
            AVG(s.base_salary + s.bonus) AS averageTotalCompensation
        FROM salary_records s
        INNER JOIN (
            SELECT employee_id, MAX(effective_from) AS max_eff
            FROM salary_records
            GROUP BY employee_id
        ) latest ON s.employee_id = latest.employee_id AND s.effective_from = latest.max_eff
        INNER JOIN employees e ON e.id = s.employee_id
        WHERE e.status = 'ACTIVE'
        GROUP BY e.department, s.currency
        ORDER BY e.department ASC, employeeCount DESC
        """, nativeQuery = true)
    List<DepartmentSalaryStatProjection> getDepartmentSalaryStatistics();
}
