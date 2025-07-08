# Jasper Reports Console - Spring Boot Application

A comprehensive Spring Boot application that demonstrates the integration of JasperReports for generating dynamic reports with mock data. This application provides REST API endpoints to generate reports in multiple formats (PDF, HTML, Excel) and includes a web interface for easy interaction.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Report Templates](#report-templates)
- [Configuration](#configuration)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## Overview

The Jasper Reports Console is a demonstration project that showcases how to integrate JasperReports with Spring Boot to create a robust reporting solution. The application generates employee reports using mock data and supports multiple output formats.

## Features

- 🚀 **Spring Boot 3.2.5** with Java 21
- 📊 **JasperReports 6.20.6** integration
- 🌐 **REST API** for report generation
- 💻 **Web Interface** for easy report access
- 📁 **Multiple Output Formats**: PDF, HTML, Excel
- 🔄 **Mock Data Service** with sample employee data
- ⚡ **Spring Boot Actuator** for monitoring
- 🎨 **Responsive Web UI** with modern styling

## Technology Stack

- **Backend Framework**: Spring Boot 3.2.5
- **Java Version**: Java 21
- **Reporting Engine**: JasperReports 6.20.6
- **Build Tool**: Maven 3.x
- **Server**: Embedded Tomcat (Port 8081)
- **Frontend**: HTML5, CSS3, JavaScript

## Project Structure

```
jasper/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/report/jasper/
│   │   │       ├── JasperApplication.java          # Main Spring Boot application
│   │   │       ├── controller/
│   │   │       │   └── ReportController.java       # REST API endpoints
│   │   │       ├── model/
│   │   │       │   └── Employee.java               # Employee data model
│   │   │       └── service/
│   │   │           ├── EmployeeDataService.java    # Mock data service
│   │   │           └── JasperReportService.java    # Report generation service
│   │   └── resources/
│   │       ├── application.properties               # Application configuration
│   │       ├── reports/
│   │       │   └── employee_report.jrxml           # JasperReports template
│   │       └── static/
│   │           └── index.html                      # Web interface
│   └── test/
│       └── java/
│           └── com/report/jasper/
│               └── JasperApplicationTests.java     # Unit tests
├── pom.xml                                         # Maven dependencies
└── README.md                                       # This documentation
```

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Git** (for cloning the repository)

## Installation & Setup

1. **Clone the repository** (if applicable):

   ```bash
   git clone <repository-url>
   cd jasper
   ```

2. **Verify Java version**:

   ```bash
   java -version
   ```

3. **Build the project**:

   ```bash
   ./mvnw clean compile
   ```

4. **Run tests** (optional):
   ```bash
   ./mvnw test
   ```

## Running the Application

### Using Maven Wrapper (Recommended)

```bash
./mvnw spring-boot:run
```

### Using Java directly

```bash
./mvnw clean package
java -jar target/jasper-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8081**

## API Endpoints

### Report Generation Endpoints

| Method | Endpoint                      | Description                              | Response Format            |
| ------ | ----------------------------- | ---------------------------------------- | -------------------------- |
| `GET`  | `/api/reports/employee/pdf`   | Generate employee report in PDF format   | `application/pdf`          |
| `GET`  | `/api/reports/employee/html`  | Generate employee report in HTML format  | `text/html`                |
| `GET`  | `/api/reports/employee/excel` | Generate employee report in Excel format | `application/vnd.ms-excel` |

### Actuator Endpoints

| Method | Endpoint           | Description               |
| ------ | ------------------ | ------------------------- |
| `GET`  | `/actuator/health` | Application health status |
| `GET`  | `/actuator/info`   | Application information   |

### Web Interface

| Method | Endpoint | Description                              |
| ------ | -------- | ---------------------------------------- |
| `GET`  | `/`      | Main web interface for report generation |

## Report Templates

### Employee Report (employee_report.jrxml)

The application includes a pre-configured JasperReports template that generates a comprehensive employee report containing:

- **Employee Information**: ID, Name, Department, Position
- **Contact Details**: Email addresses
- **Salary Information**: Current salary details
- **Professional Summary**: Years of experience and other details

The template is located at: `src/main/resources/reports/employee_report.jrxml`

## Configuration

### Application Properties

The application can be configured through `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/

# Application Configuration
spring.application.name=jasper-report-console

# HTTP Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.report.jasper=DEBUG
logging.level.net.sf.jasperreports=WARN

# Static Resources
spring.web.resources.static-locations=classpath:/static/
spring.mvc.static-path-pattern=/**
```

### Key Configuration Options

- **Port**: Change `server.port` to run on a different port
- **Context Path**: Modify `server.servlet.context-path` for custom base URL
- **Logging**: Adjust logging levels for debugging
- **File Upload**: Configure maximum file sizes if needed

## Testing

### Running Unit Tests

```bash
./mvnw test
```

### Manual Testing

1. **Start the application**:

   ```bash
   ./mvnw spring-boot:run
   ```

2. **Access the web interface**:
   Open http://localhost:8081 in your browser

3. **Test API endpoints**:

   ```bash
   # PDF Report
   curl -o employee_report.pdf http://localhost:8081/api/reports/employee/pdf

   # HTML Report
   curl http://localhost:8081/api/reports/employee/html

   # Excel Report
   curl -o employee_report.xls http://localhost:8081/api/reports/employee/excel
   ```

4. **Check application health**:
   ```bash
   curl http://localhost:8081/actuator/health
   ```

## Troubleshooting

### Common Issues

1. **Port Already in Use**:

   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **Java Version Issues**:

   ```bash
   # Verify Java 21 is installed
   java -version
   ```

3. **Maven Build Failures**:

   ```bash
   # Clean and rebuild
   ./mvnw clean compile
   ```

4. **JasperReports Template Issues**:
   - Ensure `employee_report.jrxml` is properly formatted
   - Check field names match the Employee model
   - Verify template is in the correct location

### Logging

Enable debug logging for troubleshooting:

```properties
logging.level.com.report.jasper=DEBUG
logging.level.net.sf.jasperreports=DEBUG
```

## Development

### Adding New Reports

1. **Create a new JRXML template** in `src/main/resources/reports/`
2. **Add a new model class** in `src/main/java/com/report/jasper/model/`
3. **Create a data service** in `src/main/java/com/report/jasper/service/`
4. **Add endpoints** in `ReportController.java`

### Customizing the Web Interface

The web interface is located at `src/main/resources/static/index.html` and can be customized with additional CSS and JavaScript.

## Dependencies

### Core Dependencies

- **Spring Boot Starter Web**: Web application framework
- **Spring Boot Starter Actuator**: Production-ready features
- **JasperReports**: Core reporting engine
- **JasperReports Fonts**: Additional font support

### Development Dependencies

- **Spring Boot DevTools**: Development utilities
- **Spring Boot Configuration Processor**: Configuration metadata
- **Spring Boot Starter Test**: Testing framework

## Performance Considerations

- **Memory**: JasperReports can be memory-intensive for large datasets
- **Caching**: Consider implementing report caching for frequently accessed reports
- **Async Processing**: For large reports, consider implementing asynchronous processing

## Security Considerations

- **Input Validation**: Validate all input parameters
- **Access Control**: Implement authentication and authorization as needed
- **File Access**: Restrict access to report templates and generated files

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please contact the development team or create an issue in the repository.

---

**Built with ❤️ using Spring Boot and JasperReports**
