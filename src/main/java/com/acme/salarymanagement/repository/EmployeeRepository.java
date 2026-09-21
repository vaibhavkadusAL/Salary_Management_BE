package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.repository.projection.CountryCountProjection;
import com.acme.salarymanagement.repository.projection.DepartmentCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    long countByStatus(EmploymentStatus status);

    @Query("SELECT e.country AS country, COUNT(e.id) AS count FROM Employee e GROUP BY e.country ORDER BY count DESC")
    List<CountryCountProjection> countEmployeesByCountry();

    @Query("SELECT e.department AS department, COUNT(e.id) AS count FROM Employee e GROUP BY e.department ORDER BY count DESC")
    List<DepartmentCountProjection> countEmployeesByDepartment();

    @Query("SELECT MAX(e.employeeCode) FROM Employee e WHERE e.employeeCode LIKE 'EMP-%'")
    String findMaxEmployeeCode();
}
