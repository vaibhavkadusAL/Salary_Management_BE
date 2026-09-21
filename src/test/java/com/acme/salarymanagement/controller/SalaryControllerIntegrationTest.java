package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.SalaryCreateRequest;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SalaryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Should add salary revisions and retrieve chronological history")
    void testAddSalaryRevisionAndGetHistory() throws Exception {
        // 1. Create an employee first
        EmployeeCreateRequest empReq = new EmployeeCreateRequest();
        empReq.setFirstName("Vikram");
        empReq.setLastName("Patel");
        empReq.setEmail("vikram.patel.test@acme.org");
        empReq.setCountry("India");
        empReq.setDepartment("Engineering");
        empReq.setDesignation("Software Engineer");
        empReq.setJoiningDate(LocalDate.of(2023, 1, 1));
        empReq.setStatus(EmploymentStatus.ACTIVE);
        empReq.setBaseSalary(new BigDecimal("1000000.00"));
        empReq.setBonus(new BigDecimal("100000.00"));
        empReq.setCurrency("INR");
        empReq.setEffectiveFrom(LocalDate.of(2023, 1, 1));
        empReq.setReason("Initial Offer");

        String empResponse = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empReq)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Number employeeId = objectMapper.readTree(empResponse).path("data").path("id").numberValue();

        // 2. Add a salary revision
        SalaryCreateRequest revReq = new SalaryCreateRequest();
        revReq.setBaseSalary(new BigDecimal("1250000.00"));
        revReq.setBonus(new BigDecimal("150000.00"));
        revReq.setCurrency("INR");
        revReq.setEffectiveFrom(LocalDate.of(2024, 1, 1));
        revReq.setReason("Annual Performance Promotion");

        mockMvc.perform(post("/api/employees/" + employeeId + "/salaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.baseSalary", is(1250000.00)))
            .andExpect(jsonPath("$.data.bonus", is(150000.00)))
            .andExpect(jsonPath("$.data.totalCompensation", is(1400000.00)))
            .andExpect(jsonPath("$.data.reason", is("Annual Performance Promotion")));

        // 3. GET /api/employees/{id}/salaries -> should have 2 historical records
        mockMvc.perform(get("/api/employees/" + employeeId + "/salaries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].effectiveFrom", is("2024-01-01")))
            .andExpect(jsonPath("$.data[0].baseSalary", is(1250000.00)))
            .andExpect(jsonPath("$.data[1].effectiveFrom", is("2023-01-01")))
            .andExpect(jsonPath("$.data[1].baseSalary", is(1000000.00)));

        // 4. Verify that the employee's current salary profile reflects the latest revision
        mockMvc.perform(get("/api/employees/" + employeeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.currentSalary", is(1250000.00)))
            .andExpect(jsonPath("$.data.currentTotalCompensation", is(1400000.00)));
    }

    @Test
    @DisplayName("Integration: Should reject negative salary revision with 400 Bad Request")
    void testAddSalaryRevision_NegativeAmount() throws Exception {
        EmployeeCreateRequest empReq = new EmployeeCreateRequest();
        empReq.setFirstName("Test");
        empReq.setLastName("Employee");
        empReq.setEmail("negative.check@acme.org");
        empReq.setCountry("United States");
        empReq.setDepartment("Operations");
        empReq.setDesignation("Operations Lead");
        empReq.setJoiningDate(LocalDate.now());
        empReq.setStatus(EmploymentStatus.ACTIVE);
        empReq.setBaseSalary(new BigDecimal("90000.00"));
        empReq.setCurrency("USD");
        empReq.setEffectiveFrom(LocalDate.now());

        String empResponse = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empReq)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Number employeeId = objectMapper.readTree(empResponse).path("data").path("id").numberValue();

        SalaryCreateRequest invalidReq = new SalaryCreateRequest();
        invalidReq.setBaseSalary(new BigDecimal("-50000.00")); // Negative
        invalidReq.setCurrency("USD");
        invalidReq.setEffectiveFrom(LocalDate.now());
        invalidReq.setReason("Invalid revision");

        mockMvc.perform(post("/api/employees/" + employeeId + "/salaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));
    }
}
