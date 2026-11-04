package com.gmp.defect_system.controller;

import com.gmp.defect_system.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/download-ng-data") // CSV
    public ResponseEntity<String> downloadNgDataReport() {
        String fileCsv = reportService.generateNgDataCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_QC_Defect.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(fileCsv, headers, HttpStatus.OK);
    }
    @GetMapping("/download-ng-data-pdf") //PDF
    public ResponseEntity<byte[]> downloadNgDataReportPdf() {
        byte[] filePdf = reportService.generateNgDataPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_QC_Defect.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(filePdf);
    }
    @GetMapping("/download-ng-data-excel") // XLSX
    public ResponseEntity<byte[]> downloadNgDataReportExcel() {
        byte[] fileExcel = reportService.generateNgDataExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_QC_Defect.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileExcel);
    }
    @GetMapping("/download-production-csv")
    public ResponseEntity<String> downloadProductionCsv() {
        String fileCsv = reportService.generateProductionCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_Produksi.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        return new ResponseEntity<>(fileCsv, headers, HttpStatus.OK);
    }
    @GetMapping("/download-production-pdf")
    public ResponseEntity<byte[]> downloadProductionPdf() {
        byte[] filePdf = reportService.generateProductionPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_Produksi.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(filePdf);
    }
    @GetMapping("/download-production-excel")
    public ResponseEntity<byte[]> downloadProductionExcel() {
        byte[] fileExcel = reportService.generateProductionExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Laporan_Produksi.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileExcel);
    }
}