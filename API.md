# API Documentation

## Overview

This document provides detailed information about the REST API endpoints available in the Jasper Reports Console application.

## Base URL

```
http://localhost:8081
```

## Authentication

Currently, the API does not require authentication. This is suitable for development and demonstration purposes.

## Content Types

The API supports the following content types:

- `application/json` (for JSON responses)
- `application/pdf` (for PDF reports)
- `text/html` (for HTML reports)
- `application/vnd.ms-excel` (for Excel reports)

## Endpoints

### 1. Generate Employee Report (PDF)

**Endpoint:** `GET /api/reports/employee/pdf`

**Description:** Generates an employee report in PDF format.

**Parameters:** None

**Response:**

- **Content-Type:** `application/pdf`
- **Status Code:** 200 (Success)
- **Body:** Binary PDF data

**Example Request:**

```bash
curl -o employee_report.pdf http://localhost:8081/api/reports/employee/pdf
```

**Example Response Headers:**

```
Content-Type: application/pdf
Content-Disposition: inline; filename=employee_report.pdf
Content-Length: 15234
```

---

### 2. Generate Employee Report (HTML)

**Endpoint:** `GET /api/reports/employee/html`

**Description:** Generates an employee report in HTML format.

**Parameters:** None

**Response:**

- **Content-Type:** `text/html`
- **Status Code:** 200 (Success)
- **Body:** HTML content

**Example Request:**

```bash
curl http://localhost:8081/api/reports/employee/html
```

**Example Response:**

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Employee Report</title>
    <style>
      /* JasperReports generated styles */
    </style>
  </head>
  <body>
    <!-- Report content -->
  </body>
</html>
```

---

### 3. Generate Employee Report (Excel)

**Endpoint:** `GET /api/reports/employee/excel`

**Description:** Generates an employee report in Excel format.

**Parameters:** None

**Response:**

- **Content-Type:** `application/vnd.ms-excel`
- **Status Code:** 200 (Success)
- **Body:** Binary Excel data

**Example Request:**

```bash
curl -o employee_report.xls http://localhost:8081/api/reports/employee/excel
```

**Example Response Headers:**

```
Content-Type: application/vnd.ms-excel
Content-Disposition: attachment; filename=employee_report.xls
Content-Length: 8765
```

---

### 4. Web Interface

**Endpoint:** `GET /`

**Description:** Serves the main web interface for report generation.

**Parameters:** None

**Response:**

- **Content-Type:** `text/html`
- **Status Code:** 200 (Success)
- **Body:** HTML page with report generation interface

---

### 5. Health Check

**Endpoint:** `GET /actuator/health`

**Description:** Returns the application health status.

**Parameters:** None

**Response:**

- **Content-Type:** `application/json`
- **Status Code:** 200 (Success)

**Example Response:**

```json
{
  "status": "UP"
}
```

---

### 6. Application Info

**Endpoint:** `GET /actuator/info`

**Description:** Returns application information.

**Parameters:** None

**Response:**

- **Content-Type:** `application/json`
- **Status Code:** 200 (Success)

**Example Response:**

```json
{
  "app": {
    "name": "jasper-report-console",
    "version": "0.0.1-SNAPSHOT"
  }
}
```

## Error Responses

### Common Error Codes

| Status Code | Description           | Example Response                               |
| ----------- | --------------------- | ---------------------------------------------- |
| 404         | Not Found             | `{"error": "Endpoint not found"}`              |
| 500         | Internal Server Error | `{"error": "Report generation failed"}`        |
| 503         | Service Unavailable   | `{"error": "Service temporarily unavailable"}` |

### Error Response Format

```json
{
  "timestamp": "2025-07-08T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Report generation failed",
  "path": "/api/reports/employee/pdf"
}
```

## Sample Data

The application uses mock data for employee reports. Here's the sample data structure:

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@company.com",
    "department": "Engineering",
    "position": "Senior Software Engineer",
    "salary": 95000.0,
    "hireDate": "2020-01-15"
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@company.com",
    "department": "Marketing",
    "position": "Marketing Manager",
    "salary": 75000.0,
    "hireDate": "2021-03-20"
  }
  // ... more employees
]
```

## Rate Limiting

Currently, there are no rate limiting restrictions on the API endpoints. For production use, consider implementing rate limiting based on your requirements.

## Versioning

This API currently does not implement versioning. Future versions may include versioning in the URL path (e.g., `/api/v1/reports/`).

## CORS

Cross-Origin Resource Sharing (CORS) is enabled for all origins in the current configuration. Modify this for production use to restrict access to specific domains.

## Testing the API

### Using cURL

```bash
# Test PDF generation
curl -o test_report.pdf http://localhost:8081/api/reports/employee/pdf

# Test HTML generation
curl http://localhost:8081/api/reports/employee/html

# Test Excel generation
curl -o test_report.xls http://localhost:8081/api/reports/employee/excel

# Test health endpoint
curl http://localhost:8081/actuator/health
```

### Using Postman

1. Import the endpoints into Postman
2. Set the base URL to `http://localhost:8081`
3. Create requests for each endpoint
4. Test different output formats

### Using the Web Interface

1. Navigate to `http://localhost:8081` in your browser
2. Click on the report format buttons (PDF, HTML, Excel)
3. The reports will be generated and displayed/downloaded

## Performance Notes

- PDF generation typically takes 100-500ms for small datasets
- HTML generation is usually the fastest (50-200ms)
- Excel generation may take longer for complex formatting (200-800ms)
- Memory usage scales with the size of the dataset and report complexity
