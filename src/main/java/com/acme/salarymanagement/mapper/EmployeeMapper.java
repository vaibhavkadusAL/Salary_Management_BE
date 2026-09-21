package com.acme.salarymanagement.mapper;

import com.acme.salarymanagement.dto.EmployeeDto;
import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.SalaryRecord;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDto toDto(Employee entity) {
        if (entity == null) {
            return null;
        }

        EmployeeDto dto = new EmployeeDto();
        dto.setId(entity.getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setCountry(entity.getCountry());
        dto.setDepartment(entity.getDepartment());
        dto.setDesignation(entity.getDesignation());
        dto.setJoiningDate(entity.getJoiningDate());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        SalaryRecord currentSalary = entity.getCurrentSalaryRecord();
        if (currentSalary != null) {
            dto.setCurrentSalaryRecordId(currentSalary.getId());
            dto.setCurrentSalary(currentSalary.getBaseSalary());
            dto.setCurrentBonus(currentSalary.getBonus());
            dto.setCurrentTotalCompensation(currentSalary.getTotalCompensation());
            dto.setCurrency(currentSalary.getCurrency());
            dto.setSalaryEffectiveDate(currentSalary.getEffectiveFrom());
            dto.setSalaryChangeReason(currentSalary.getReason());
        }

        return dto;
    }
}
