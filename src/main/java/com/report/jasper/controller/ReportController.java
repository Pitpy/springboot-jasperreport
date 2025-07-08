package com.report.jasper.controller;

import com.report.jasper.model.Employee;
import com.report.jasper.model.ReportRequest;
import com.report.jasper.service.EmployeeDataService;
import com.report.jasper.service.JasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private JasperReportService jasperReportService;

    @Autowired
    private EmployeeDataService employeeDataService;

    @GetMapping("/employee")
    public ResponseEntity<byte[]> generateEmployeeReport(
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            List<Employee> employees = employeeDataService.getMockEmployeeData();

            byte[] reportBytes = switch (format.toLowerCase()) {
                case "pdf" -> jasperReportService.generatePdfReport(employees, "employee_report");
                case "xlsx" -> jasperReportService.generateExcelReport(employees, "employee_report");
                case "html" -> jasperReportService.generateHtmlReport(employees, "employee_report");
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "employee_report_" + timestamp + jasperReportService.getFileExtension(format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(jasperReportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportBytes.length);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/employee/department/{department}")
    public ResponseEntity<byte[]> generateEmployeeReportByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            List<Employee> employees = employeeDataService.getEmployeesByDepartment(department);

            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("No employees found in department: " + department).getBytes());
            }

            byte[] reportBytes = switch (format.toLowerCase()) {
                case "pdf" -> jasperReportService.generatePdfReport(employees, "employee_report");
                case "xlsx" -> jasperReportService.generateExcelReport(employees, "employee_report");
                case "html" -> jasperReportService.generateHtmlReport(employees, "employee_report");
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "employee_report_" + department.toLowerCase() + "_" + timestamp +
                    jasperReportService.getFileExtension(format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(jasperReportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportBytes.length);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/employee/salary")
    public ResponseEntity<byte[]> generateEmployeeReportBySalaryRange(
            @RequestParam(defaultValue = "0") Double minSalary,
            @RequestParam(defaultValue = "999999") Double maxSalary,
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            List<Employee> employees = employeeDataService.getEmployeesWithSalaryRange(minSalary, maxSalary);

            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("No employees found with salary between " + minSalary + " and " + maxSalary).getBytes());
            }

            byte[] reportBytes = switch (format.toLowerCase()) {
                case "pdf" -> jasperReportService.generatePdfReport(employees, "employee_report");
                case "xlsx" -> jasperReportService.generateExcelReport(employees, "employee_report");
                case "html" -> jasperReportService.generateHtmlReport(employees, "employee_report");
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "employee_salary_report_" + timestamp + jasperReportService.getFileExtension(format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(jasperReportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportBytes.length);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/employee/data")
    public ResponseEntity<List<Employee>> getEmployeeData() {
        try {
            List<Employee> employees = employeeDataService.getMockEmployeeData();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/formats")
    public ResponseEntity<String[]> getSupportedFormats() {
        return ResponseEntity.ok(new String[] { "pdf", "xlsx", "html" });
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "jasper-report-console");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("port", "8081");

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "*")
                .body(health);
    }

    @PostMapping("/employee/custom")
    public ResponseEntity<byte[]> generateCustomEmployeeReport(
            @RequestBody List<Employee> employees,
            @RequestParam(defaultValue = "pdf") String format) {
        try {
            if (employees == null || employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Employee list cannot be empty".getBytes());
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("creator", "Created by: Some User");

            byte[] reportBytes = switch (format.toLowerCase()) {
                case "pdf" ->
                    jasperReportService.generateReportWithParametersPdf(employees, parameters, "users", "pdf");
                case "xlsx" -> jasperReportService.generateExcelReport(employees, "users");
                case "html" -> jasperReportService.generateHtmlReport(employees, "users");
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "custom_user_report_" + timestamp + jasperReportService.getFileExtension(format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(jasperReportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportBytes.length);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating custom report: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/employee/generate")
    public ResponseEntity<byte[]> generateReportFromRequest(@RequestBody ReportRequest request) {
        try {
            List<Employee> employees;

            // If employees are provided in request, use them; otherwise get from service
            // with filters
            if (request.getEmployees() != null && !request.getEmployees().isEmpty()) {
                employees = request.getEmployees();
            } else {
                // Get all employees and apply filters if provided
                employees = employeeDataService.getMockEmployeeData();

                if (request.getFilters() != null) {
                    ReportRequest.ReportFilters filters = request.getFilters();

                    // Apply department filter
                    if (filters.getDepartment() != null && !filters.getDepartment().isEmpty()) {
                        employees = employees.stream()
                                .filter(emp -> emp.getDepartment().equalsIgnoreCase(filters.getDepartment()))
                                .toList();
                    }

                    // Apply salary range filter
                    if (filters.getMinSalary() != null) {
                        employees = employees.stream()
                                .filter(emp -> emp.getSalary() >= filters.getMinSalary())
                                .toList();
                    }

                    if (filters.getMaxSalary() != null) {
                        employees = employees.stream()
                                .filter(emp -> emp.getSalary() <= filters.getMaxSalary())
                                .toList();
                    }
                }
            }

            if (employees.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No employees found matching the criteria".getBytes());
            }

            String format = request.getFormat() != null ? request.getFormat() : "pdf";
            String reportTemplate = request.getReportName() != null ? request.getReportName() : "employee_report";

            byte[] reportBytes = switch (format.toLowerCase()) {
                case "pdf" -> jasperReportService.generatePdfReport(employees, reportTemplate);
                case "xlsx" -> jasperReportService.generateExcelReport(employees, reportTemplate);
                case "html" -> jasperReportService.generateHtmlReport(employees, reportTemplate);
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "generated_report_" + timestamp + jasperReportService.getFileExtension(format);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(jasperReportService.getContentType(format)));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportBytes.length);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
}
