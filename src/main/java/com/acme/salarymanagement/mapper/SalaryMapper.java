package com.acme.salarymanagement.mapper;

import com.acme.salarymanagement.dto.EmployeeDto;
import com.acme.salarymanagement.dto.SalaryRecordDto;
import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.SalaryRecord;
import org.springframework.stereotype.Component;

@Component
public class SalaryMapper {

    public SalaryRecordDto toDto(SalaryRecord entity) {
        if (entity == null) {
            return null;
        }

        SalaryRecordDto dto = new SalaryRecordDto();
        dto.setId(entity.getId());
        if (entity.getEmployee() != null) {
            dto.setEmployeeId(entity.getEmployee().getId());
            dto.setEmployeeCode(entity.getEmployee().getEmployeeCode());
        }
        dto.setBaseSalary(entity.getBaseSalary());
        dto.setBonus(entity.getBonus());
        dto.setTotalCompensation(entity.getTotalCompensation());
        dto.setCurrency(entity.getCurrency());
        dto.setEffectiveFrom(entity.getEffectiveFrom());
        dto.setReason(entity.getReason());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
