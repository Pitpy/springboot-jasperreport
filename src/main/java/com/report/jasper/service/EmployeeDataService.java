package com.report.jasper.service;

import com.report.jasper.model.Employee;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EmployeeDataService {

    public List<Employee> getMockEmployeeData() {
        return Arrays.asList(
                new Employee(1L, "John Smith", "john.smith@company.com", "Engineering", 75000.0, "2022-01-15"),
                new Employee(2L, "Sarah Johnson", "sarah.johnson@company.com", "Marketing", 65000.0, "2022-03-20"),
                new Employee(3L, "Michael Brown", "michael.brown@company.com", "Sales", 60000.0, "2021-11-10"),
                new Employee(4L, "Emily Davis", "emily.davis@company.com", "HR", 55000.0, "2023-02-05"),
                new Employee(5L, "David Wilson", "david.wilson@company.com", "Engineering", 80000.0, "2021-09-12"),
                new Employee(6L, "Lisa Anderson", "lisa.anderson@company.com", "Finance", 70000.0, "2022-07-18"),
                new Employee(7L, "Robert Garcia", "robert.garcia@company.com", "Engineering", 78000.0, "2023-01-25"),
                new Employee(8L, "Jennifer Martinez", "jennifer.martinez@company.com", "Marketing", 62000.0,
                        "2022-05-30"),
                new Employee(9L, "Christopher Lee", "christopher.lee@company.com", "Sales", 58000.0, "2023-04-14"),
                new Employee(10L, "Amanda Taylor", "amanda.taylor@company.com", "Operations", 67000.0, "2021-12-08"),
                new Employee(11L, "James Thompson", "james.thompson@company.com", "Engineering", 82000.0, "2020-10-22"),
                new Employee(12L, "Michelle White", "michelle.white@company.com", "HR", 59000.0, "2022-08-03"),
                new Employee(13L, "Kevin Harris", "kevin.harris@company.com", "Finance", 72000.0, "2021-06-17"),
                new Employee(14L, "Rachel Clark", "rachel.clark@company.com", "Marketing", 64000.0, "2023-03-11"),
                new Employee(15L, "Daniel Lewis", "daniel.lewis@company.com", "Sales", 61000.0, "2022-09-26"));
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return getMockEmployeeData().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .toList();
    }

    public List<Employee> getEmployeesWithSalaryRange(Double minSalary, Double maxSalary) {
        return getMockEmployeeData().stream()
                .filter(emp -> emp.getSalary() >= minSalary && emp.getSalary() <= maxSalary)
                .toList();
    }
}
