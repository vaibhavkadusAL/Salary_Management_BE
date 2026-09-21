package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.EmployeeUpdateRequest;
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
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integration: Should create employee, retrieve it, and verify pagination")
    void testCreateAndRetrieveEmployee() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setFirstName("Rahul");
        request.setLastName("Sharma");
        request.setEmail("rahul.sharma.test@acme.org");
        request.setCountry("India");
        request.setDepartment("Engineering");
        request.setDesignation("Staff Software Engineer");
        request.setJoiningDate(LocalDate.of(2023, 6, 1));
        request.setStatus(EmploymentStatus.ACTIVE);
        request.setBaseSalary(new BigDecimal("2200000.00"));
        request.setBonus(new BigDecimal("300000.00"));
        request.setCurrency("INR");
        request.setEffectiveFrom(LocalDate.of(2023, 6, 1));
        request.setReason("Joining package");

        // 1. POST /api/employees
        String responseContent = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.employeeCode", startsWith("EMP-")))
            .andExpect(jsonPath("$.data.email", is("rahul.sharma.test@acme.org")))
            .andExpect(jsonPath("$.data.currentSalary", is(2200000.00)))
            .andReturn().getResponse().getContentAsString();

        Number empId = objectMapper.readTree(responseContent).path("data").path("id").numberValue();

        // 2. GET /api/employees/{id}
        mockMvc.perform(get("/api/employees/" + empId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.fullName", is("Rahul Sharma")))
            .andExpect(jsonPath("$.data.currency", is("INR")));

        // 3. GET /api/employees?search=rahul
        mockMvc.perform(get("/api/employees")
                .param("search", "rahul")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.data[0].email", is("rahul.sharma.test@acme.org")));
    }

    @Test
    @DisplayName("Integration: Should return 400 Bad Request when mandatory fields are missing")
    void testCreateEmployee_ValidationFailure() throws Exception {
        EmployeeCreateRequest invalidReq = new EmployeeCreateRequest();
        invalidReq.setFirstName(""); // Blank
        invalidReq.setEmail("not-an-email"); // Invalid email

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")))
            .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Integration: Should return 409 Conflict when creating duplicate email")
    void testCreateEmployee_DuplicateEmail() throws Exception {
        EmployeeCreateRequest request1 = new EmployeeCreateRequest();
        request1.setFirstName("First");
        request1.setLastName("User");
        request1.setEmail("duplicate.check@acme.org");
        request1.setCountry("United States");
        request1.setDepartment("Engineering");
        request1.setDesignation("Engineer");
        request1.setJoiningDate(LocalDate.now());
        request1.setStatus(EmploymentStatus.ACTIVE);
        request1.setBaseSalary(new BigDecimal("100000.00"));
        request1.setCurrency("USD");
        request1.setEffectiveFrom(LocalDate.now());

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        // Attempt second create with identical email
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error", is("DUPLICATE_ENTITY")));
    }

    @Test
    @DisplayName("Integration: Should update existing employee metadata")
    void testUpdateEmployee() throws Exception {
        EmployeeCreateRequest createReq = new EmployeeCreateRequest();
        createReq.setFirstName("Initial");
        createReq.setLastName("Name");
        createReq.setEmail("update.test@acme.org");
        createReq.setCountry("Canada");
        createReq.setDepartment("Sales");
        createReq.setDesignation("Sales Representative");
        createReq.setJoiningDate(LocalDate.now());
        createReq.setStatus(EmploymentStatus.ACTIVE);
        createReq.setBaseSalary(new BigDecimal("80000.00"));
        createReq.setCurrency("CAD");
        createReq.setEffectiveFrom(LocalDate.now());

        String createdJson = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        Number id = objectMapper.readTree(createdJson).path("data").path("id").numberValue();

        EmployeeUpdateRequest updateReq = new EmployeeUpdateRequest();
        updateReq.setFirstName("Updated");
        updateReq.setLastName("Name");
        updateReq.setEmail("update.test@acme.org");
        updateReq.setCountry("Canada");
        updateReq.setDepartment("Sales");
        updateReq.setDesignation("Senior Account Executive");
        updateReq.setStatus(EmploymentStatus.ACTIVE);

        mockMvc.perform(put("/api/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.firstName", is("Updated")))
            .andExpect(jsonPath("$.data.designation", is("Senior Account Executive")));
    }
}
