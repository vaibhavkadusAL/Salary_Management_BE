package com.acme.salarymanagement.repository.specification;

import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.entity.SalaryRecord;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> filterBy(
        String search,
        String country,
        String department,
        String designation,
        EmploymentStatus status,
        BigDecimal minSalary,
        BigDecimal maxSalary
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Text Search across code, first name, last name, and email
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate codePredicate = cb.like(cb.lower(root.get("employeeCode")), searchPattern);
                Predicate firstNamePredicate = cb.like(cb.lower(root.get("firstName")), searchPattern);
                Predicate lastNamePredicate = cb.like(cb.lower(root.get("lastName")), searchPattern);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);

                predicates.add(cb.or(codePredicate, firstNamePredicate, lastNamePredicate, emailPredicate));
            }

            // 2. Country filter
            if (country != null && !country.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("country")), country.trim().toLowerCase()));
            }

            // 3. Department filter
            if (department != null && !department.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("department")), department.trim().toLowerCase()));
            }

            // 4. Designation filter
            if (designation != null && !designation.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("designation")), designation.trim().toLowerCase()));
            }

            // 5. Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 6. Salary Range filters (minSalary / maxSalary)
            if (minSalary != null || maxSalary != null) {
                Join<Employee, SalaryRecord> salaryJoin = root.join("salaryRecords", JoinType.INNER);

                // Subquery to ensure we only compare against the latest salary record
                Subquery<LocalDate> subquery = query.subquery(LocalDate.class);
                Root<SalaryRecord> subRoot = subquery.from(SalaryRecord.class);
                subquery.select(cb.greatest(subRoot.<LocalDate>get("effectiveFrom")))
                    .where(cb.equal(subRoot.get("employee"), root));

                predicates.add(cb.equal(salaryJoin.get("effectiveFrom"), subquery));

                if (minSalary != null) {
                    predicates.add(cb.greaterThanOrEqualTo(salaryJoin.get("baseSalary"), minSalary));
                }
                if (maxSalary != null) {
                    predicates.add(cb.lessThanOrEqualTo(salaryJoin.get("baseSalary"), maxSalary));
                }

                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
