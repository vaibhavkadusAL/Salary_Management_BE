package com.acme.salarymanagement.service;

import com.acme.salarymanagement.entity.Employee;
import com.acme.salarymanagement.entity.EmploymentStatus;
import com.acme.salarymanagement.entity.SalaryRecord;
import com.acme.salarymanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSeedingService implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.seed.target-count:10000}")
    private int targetCount;

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Database seeding is disabled in configuration.");
            return;
        }

        long existingCount = employeeRepository.count();
        if (existingCount >= targetCount) {
            log.info("Database already contains {} employee records. Skipping seeding.", existingCount);
            return;
        }

        int recordsToSeed = (int) (targetCount - existingCount);
        log.info("Starting high-performance seed of {} employee records into MySQL...", recordsToSeed);
        long startTime = System.currentTimeMillis();

        seedBatch(recordsToSeed, (int) existingCount);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Successfully seeded {} records into MySQL in {} ms (Total: {})",
            recordsToSeed, duration, employeeRepository.count());
    }

    public void seedBatch(int count, int startOffset) {
        Random rand = new Random(42); // Deterministic seed

        String[] firstNames = {
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
            "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen",
            "Rahul", "Priya", "Amit", "Sneha", "Vikram", "Ananya", "Rohan", "Deepika", "Aditya", "Pooja",
            "Kavita", "Suresh", "Sunita", "Rajesh", "Meera", "Arun", "Divya", "Sanjay", "Neha", "Manoj",
            "Alexander", "Sophie", "Lucas", "Emma", "Oliver", "Charlotte", "Liam", "Amelia", "Noah", "Ava",
            "Maximilian", "Hannah", "Felix", "Mia", "Leon", "Lea", "Paul", "Lina", "Jonas", "Ella"
        };

        String[] lastNames = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Sharma", "Patel", "Verma", "Gupta", "Kumar", "Singh", "Reddy", "Nair", "Chopra", "Iyer",
            "Taylor", "Davies", "Evans", "Thomas", "Wilson", "Roberts", "Clarke", "Wright", "Walker", "White",
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
            "Tan", "Lim", "Lee", "Ng", "Ong", "Wong", "Goh", "Chua", "Chan", "Koh"
        };

        class CountryConfig {
            final String country;
            final String currency;
            final int minBase;
            final int maxBase;
            final int baseStep;

            CountryConfig(String country, String currency, int minBase, int maxBase, int baseStep) {
                this.country = country;
                this.currency = currency;
                this.minBase = minBase;
                this.maxBase = maxBase;
                this.baseStep = baseStep;
            }
        }

        CountryConfig[] countries = {
            new CountryConfig("United States", "USD", 75000, 220000, 5000),
            new CountryConfig("India", "INR", 600000, 3500000, 50000),
            new CountryConfig("United Kingdom", "GBP", 45000, 130000, 3000),
            new CountryConfig("Germany", "EUR", 55000, 145000, 4000),
            new CountryConfig("Canada", "CAD", 70000, 175000, 5000),
            new CountryConfig("Singapore", "SGD", 65000, 190000, 5000),
            new CountryConfig("Australia", "AUD", 80000, 195000, 5000)
        };

        Map<String, String[]> deptDesignations = new LinkedHashMap<>();
        deptDesignations.put("Engineering", new String[]{
            "Junior Software Engineer", "Software Engineer", "Senior Software Engineer", "Staff Engineer", "Principal Engineer", "Engineering Manager"
        });
        deptDesignations.put("Product", new String[]{
            "Associate Product Manager", "Product Manager", "Senior Product Manager", "Director of Product"
        });
        deptDesignations.put("Human Resources", new String[]{
            "HR Coordinator", "HR Generalist", "Senior HR Specialist", "HR Business Partner", "Director of People"
        });
        deptDesignations.put("Finance", new String[]{
            "Financial Analyst", "Senior Financial Analyst", "Finance Manager", "Controller", "VP of Finance"
        });
        deptDesignations.put("Marketing", new String[]{
            "Marketing Specialist", "Content Strategist", "Growth Marketing Lead", "VP of Marketing"
        });
        deptDesignations.put("Sales", new String[]{
            "Account Executive", "Senior Account Executive", "Sales Director", "Regional VP of Sales"
        });
        deptDesignations.put("Operations", new String[]{
            "Operations Associate", "Operations Manager", "Director of Business Operations"
        });
        deptDesignations.put("Legal", new String[]{
            "Legal Counsel", "Senior Corporate Counsel", "Head of Compliance"
        });

        List<String> departmentList = new ArrayList<>(deptDesignations.keySet());

        int batchSize = 1000;
        List<Employee> currentBatch = new ArrayList<>(batchSize);

        for (int i = 1; i <= count; i++) {
            int seqNum = startOffset + i;
            String employeeCode = String.format("EMP-%05d", seqNum);

            String firstName = firstNames[rand.nextInt(firstNames.length)];
            String lastName = lastNames[rand.nextInt(lastNames.length)];
            String email = String.format("%s.%s.%d@acme.org",
                firstName.toLowerCase().replaceAll("[^a-z]", ""),
                lastName.toLowerCase().replaceAll("[^a-z]", ""),
                seqNum
            );

            CountryConfig countryCfg = countries[rand.nextInt(countries.length)];
            String department = departmentList.get(rand.nextInt(departmentList.size()));
            String[] designations = deptDesignations.get(department);
            String designation = designations[rand.nextInt(designations.length)];

            // Joining date between 2021-01-01 and 2026-06-01
            int daysOffset = rand.nextInt(1950);
            LocalDate joiningDate = LocalDate.of(2021, 1, 1).plusDays(daysOffset);

            // 95% Active, 5% Inactive
            EmploymentStatus status = (rand.nextInt(100) < 95) ? EmploymentStatus.ACTIVE : EmploymentStatus.INACTIVE;

            Employee employee = new Employee(
                employeeCode, firstName, lastName, email,
                countryCfg.country, department, designation, joiningDate, status
            );

            // Base salary calculation
            int steps = (countryCfg.maxBase - countryCfg.minBase) / countryCfg.baseStep;
            int initialBase = countryCfg.minBase + rand.nextInt(steps + 1) * countryCfg.baseStep;
            int initialBonus = (int) (initialBase * (0.05 + rand.nextDouble() * 0.15));

            // Initial salary record
            SalaryRecord initialSalary = new SalaryRecord(
                employee,
                BigDecimal.valueOf(initialBase).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(initialBonus).setScale(2, RoundingMode.HALF_UP),
                countryCfg.currency,
                joiningDate,
                "Initial compensation"
            );
            employee.addSalaryRecord(initialSalary);

            // ~35% of employees have had a subsequent salary revision in 2024 or 2025
            if (rand.nextInt(100) < 35 && joiningDate.isBefore(LocalDate.of(2024, 1, 1))) {
                LocalDate revisionDate = joiningDate.plusYears(1).plusMonths(rand.nextInt(6));
                int revisedBase = (int) (initialBase * 1.10); // 10% raise
                int revisedBonus = (int) (revisedBase * 0.12);

                SalaryRecord revisionRecord = new SalaryRecord(
                    employee,
                    BigDecimal.valueOf(revisedBase).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(revisedBonus).setScale(2, RoundingMode.HALF_UP),
                    countryCfg.currency,
                    revisionDate,
                    "Annual salary revision"
                );
                employee.addSalaryRecord(revisionRecord);
            }

            currentBatch.add(employee);

            if (currentBatch.size() >= batchSize) {
                saveBatch(currentBatch);
                currentBatch.clear();
                log.info("Seeded batch {}/{}", seqNum, count + startOffset);
            }
        }

        if (!currentBatch.isEmpty()) {
            saveBatch(currentBatch);
            currentBatch.clear();
        }
    }

    @Transactional
    public void saveBatch(List<Employee> batch) {
        employeeRepository.saveAll(batch);
    }
}
