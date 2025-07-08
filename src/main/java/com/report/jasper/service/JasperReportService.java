package com.report.jasper.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    public byte[] generatePdfReport(List<?> data, String reportName) throws Exception {
        return generateReport(data, reportName, "pdf");
    }

    public byte[] generateExcelReport(List<?> data, String reportName) throws Exception {
        return generateReport(data, reportName, "xlsx");
    }

    public byte[] generateHtmlReport(List<?> data, String reportName) throws Exception {
        return generateReport(data, reportName, "html");
    }

    private byte[] generateReport(List<?> data, String reportName, String format) throws Exception {
        try {
            // Load the JRXML file
            ClassPathResource resource = new ClassPathResource("reports/" + reportName + ".jrxml");
            if (!resource.exists()) {
                throw new Exception("JRXML template not found: " + reportName + ".jrxml");
            }

            InputStream reportStream = resource.getInputStream();

            // Compile the report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Parameters for the report (can be extended as needed)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("reportTitle", "Employee Report");
            parameters.put("generatedBy", "Jasper Report Console");

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export based on format
            return switch (format.toLowerCase()) {
                case "pdf" -> JasperExportManager.exportReportToPdf(jasperPrint);
                case "xlsx" -> {
                    net.sf.jasperreports.export.SimpleExporterInput exporterInput = new net.sf.jasperreports.export.SimpleExporterInput(
                            jasperPrint);

                    java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
                    net.sf.jasperreports.export.SimpleOutputStreamExporterOutput exporterOutput = new net.sf.jasperreports.export.SimpleOutputStreamExporterOutput(
                            outputStream);

                    net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter exporter = new net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter();
                    exporter.setExporterInput(exporterInput);
                    exporter.setExporterOutput(exporterOutput);

                    net.sf.jasperreports.export.SimpleXlsxReportConfiguration configuration = new net.sf.jasperreports.export.SimpleXlsxReportConfiguration();
                    configuration.setOnePagePerSheet(false);
                    configuration.setDetectCellType(true);
                    exporter.setConfiguration(configuration);

                    exporter.exportReport();
                    yield outputStream.toByteArray();
                }
                case "html" -> {
                    net.sf.jasperreports.export.SimpleExporterInput exporterInput = new net.sf.jasperreports.export.SimpleExporterInput(
                            jasperPrint);

                    java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
                    net.sf.jasperreports.export.SimpleHtmlExporterOutput exporterOutput = new net.sf.jasperreports.export.SimpleHtmlExporterOutput(
                            outputStream);

                    net.sf.jasperreports.engine.export.HtmlExporter exporter = new net.sf.jasperreports.engine.export.HtmlExporter();
                    exporter.setExporterInput(exporterInput);
                    exporter.setExporterOutput(exporterOutput);

                    net.sf.jasperreports.export.SimpleHtmlReportConfiguration configuration = new net.sf.jasperreports.export.SimpleHtmlReportConfiguration();
                    // Use default HTML configuration
                    exporter.setConfiguration(configuration);

                    exporter.exportReport();
                    yield outputStream.toByteArray();
                }
                default -> throw new IllegalArgumentException("Unsupported format: " + format);
            };

        } catch (Exception e) {
            throw new Exception("Error generating " + format.toUpperCase() + " report: " + e.getMessage(), e);
        }
    }

    public String getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "html" -> "text/html";
            default -> "application/octet-stream";
        };
    }

    public String getFileExtension(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> ".pdf";
            case "xlsx" -> ".xlsx";
            case "html" -> ".html";
            default -> ".dat";
        };
    }
}
