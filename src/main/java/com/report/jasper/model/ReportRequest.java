package com.report.jasper.model;

import java.util.List;

public class ReportRequest {
    private List<Employee> employees;
    private String format;
    private String reportName;
    private ReportFilters filters;

    public ReportRequest() {
    }

    public ReportRequest(List<Employee> employees, String format, String reportName, ReportFilters filters) {
        this.employees = employees;
        this.format = format;
        this.reportName = reportName;
        this.filters = filters;
    }

    // Getters and Setters
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public ReportFilters getFilters() {
        return filters;
    }

    public void setFilters(ReportFilters filters) {
        this.filters = filters;
    }

    public static class ReportFilters {
        private String department;
        private Double minSalary;
        private Double maxSalary;
        private String joinDateFrom;
        private String joinDateTo;

        public ReportFilters() {
        }

        // Getters and Setters
        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public Double getMinSalary() {
            return minSalary;
        }

        public void setMinSalary(Double minSalary) {
            this.minSalary = minSalary;
        }

        public Double getMaxSalary() {
            return maxSalary;
        }

        public void setMaxSalary(Double maxSalary) {
            this.maxSalary = maxSalary;
        }

        public String getJoinDateFrom() {
            return joinDateFrom;
        }

        public void setJoinDateFrom(String joinDateFrom) {
            this.joinDateFrom = joinDateFrom;
        }

        public String getJoinDateTo() {
            return joinDateTo;
        }

        public void setJoinDateTo(String joinDateTo) {
            this.joinDateTo = joinDateTo;
        }
    }
}
