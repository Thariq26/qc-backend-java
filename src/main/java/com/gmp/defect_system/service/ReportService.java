package com.gmp.defect_system.service;

import com.gmp.defect_system.entity.NgData;
import com.gmp.defect_system.entity.Production;
import com.gmp.defect_system.entity.CustomerComplaint;
import com.gmp.defect_system.repository.NgDataRepository;
import com.gmp.defect_system.repository.ProductionRepository;
import com.gmp.defect_system.repository.CustomerComplaintRepository;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {
    private final NgDataRepository ngDataRepository;
    private final ProductionRepository productionRepository;
    private final CustomerComplaintRepository complaintRepository;
    public ReportService(NgDataRepository ngDataRepository,
                         ProductionRepository productionRepository,
                         CustomerComplaintRepository complaintRepository) {
        this.ngDataRepository = ngDataRepository;
        this.productionRepository = productionRepository;
        this.complaintRepository = complaintRepository;
    }
    // 1. Defect - CSV
    public String generateNgDataCsv() {
        List<NgData> semuaNg = ngDataRepository.findAllByOrderByIdAsc();
        List<CustomerComplaint> semuaKomplain = complaintRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();

        // Bagian 1: Internal
        csvBuilder.append("--- DATA DEFECT INTERNAL ---\n");
        csvBuilder.append("ID,Tanggal,Part Number,Nama Part,Jenis Cacat,Quantity,Operator,Keterangan\n");
        for (NgData data : semuaNg) {
            csvBuilder.append(data.getId()).append(",")
                    .append(data.getDefectDate()).append(",")
                    .append(data.getPart().getPartNumber()).append(",")
                    .append(data.getPart().getPartName()).append(",")
                    .append(data.getDefectType().getName()).append(",")
                    .append(data.getQuantity()).append(",")
                    .append(data.getOperatorName()).append(",")
                    .append(data.getRemarks() != null ? data.getRemarks().replace(",", " ") : "")
                    .append("\n");
        }

        // Bagian 2: Customer Complaint
        csvBuilder.append("\n--- DATA KOMPLAIN CUSTOMER ---\n");
        csvBuilder.append("ID,Tanggal,Customer,Nama Part,Jenis Cacat,Quantity,Lot Number,Deskripsi Masalah\n");
        for (CustomerComplaint data : semuaKomplain) {
            csvBuilder.append(data.getId()).append(",")
                    .append(data.getComplaintDate()).append(",")
                    .append(data.getCustomerName() != null ? data.getCustomerName().replace(",", " ") : "").append(",")
                    .append(data.getPart().getPartName()).append(",")
                    .append(data.getDefectType().getName()).append(",")
                    .append(data.getDefectQuantity()).append(",")
                    .append(data.getLotNumber()).append(",")
                    .append(data.getProblemDescription() != null ? data.getProblemDescription().replace(",", " ") : "")
                    .append("\n");
        }
        return csvBuilder.toString();
    }

    // 2. Defect - PDF
    public byte[] generateNgDataPdf() {
        try {
            List<NgData> semuaNg = ngDataRepository.findAllByOrderByIdAsc();
            List<CustomerComplaint> semuaKomplain = complaintRepository.findAll();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // --- TABEL 1: INTERNAL DEFECT ---
            Font fontJudul = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph judul1 = new Paragraph("Laporan QC Defect Monitoring (Internal)", fontJudul);
            judul1.setAlignment(Element.ALIGN_CENTER);
            document.add(judul1);
            document.add(Chunk.NEWLINE);

            PdfPTable tableNg = new PdfPTable(7);
            tableNg.setWidthPercentage(100);
            tableNg.setWidths(new float[]{1f, 2f, 3f, 3f, 1.5f, 2f, 3f});

            String[] headersNg = {"ID", "Tanggal", "Nama Part", "Jenis Cacat", "Qty", "Operator", "Keterangan"};
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String headerTitle : headersNg) {
                PdfPCell cell = new PdfPCell(new Phrase(headerTitle, fontHeader));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tableNg.addCell(cell);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (NgData data : semuaNg) {
                tableNg.addCell(new PdfPCell(new Phrase(String.valueOf(data.getId()), fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(data.getDefectDate() != null ? data.getDefectDate().toString() : "-", fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(data.getPart().getPartName(), fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(data.getDefectType().getName(), fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(String.valueOf(data.getQuantity()), fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(data.getOperatorName(), fontData)));
                tableNg.addCell(new PdfPCell(new Phrase(data.getRemarks() != null ? data.getRemarks() : "-", fontData)));
            }
            document.add(tableNg);

            // Jarak antar tabel
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // --- TABEL 2: CUSTOMER COMPLAINT ---
            Paragraph judul2 = new Paragraph("Laporan Customer Complaint (Eksternal)", fontJudul);
            judul2.setAlignment(Element.ALIGN_CENTER);
            document.add(judul2);
            document.add(Chunk.NEWLINE);

            PdfPTable tableKomplain = new PdfPTable(7);
            tableKomplain.setWidthPercentage(100);
            tableKomplain.setWidths(new float[]{1f, 2f, 3f, 3f, 2.5f, 1.5f, 3f});

            String[] headersKomplain = {"ID", "Tanggal", "Customer", "Nama Part", "Jenis Cacat", "Qty", "Deskripsi"};
            for (String headerTitle : headersKomplain) {
                PdfPCell cell = new PdfPCell(new Phrase(headerTitle, fontHeader));
                cell.setBackgroundColor(new Color(255, 204, 204));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                tableKomplain.addCell(cell);
            }

            for (CustomerComplaint data : semuaKomplain) {
                tableKomplain.addCell(new PdfPCell(new Phrase(String.valueOf(data.getId()), fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(data.getComplaintDate() != null ? data.getComplaintDate().toString() : "-", fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(data.getCustomerName(), fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(data.getPart().getPartName(), fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(data.getDefectType().getName(), fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(String.valueOf(data.getDefectQuantity()), fontData)));
                tableKomplain.addCell(new PdfPCell(new Phrase(data.getProblemDescription() != null ? data.getProblemDescription() : "-", fontData)));
            }
            document.add(tableKomplain);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat file PDF NG Data: " + e.getMessage());
        }
    }

    // 3. Defect - EXCEL
    public byte[] generateNgDataExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // --- SHEET 1: INTERNAL DEFECT ---
            List<NgData> semuaNg = ngDataRepository.findAllByOrderByIdAsc();
            Sheet sheet1 = workbook.createSheet("Defect Internal");

            CellStyle headerStyle1 = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            excelFont.setBold(true);
            headerStyle1.setFont(excelFont);
            headerStyle1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow1 = sheet1.createRow(0);
            String[] columns1 = {"ID", "Tanggal", "Part Number", "Nama Part", "Jenis Cacat", "Quantity", "Operator", "Keterangan"};

            for (int i = 0; i < columns1.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(columns1[i]);
                cell.setCellStyle(headerStyle1);
            }

            int rowIdx1 = 1;
            for (NgData data : semuaNg) {
                Row row = sheet1.createRow(rowIdx1++);
                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getDefectDate() != null ? data.getDefectDate().toString() : "");
                row.createCell(2).setCellValue(data.getPart().getPartNumber());
                row.createCell(3).setCellValue(data.getPart().getPartName());
                row.createCell(4).setCellValue(data.getDefectType().getName());
                row.createCell(5).setCellValue(data.getQuantity());
                row.createCell(6).setCellValue(data.getOperatorName());
                row.createCell(7).setCellValue(data.getRemarks() != null ? data.getRemarks() : "-");
            }
            for (int i = 0; i < columns1.length; i++) sheet1.autoSizeColumn(i);


            // --- SHEET 2: CUSTOMER COMPLAINT ---
            List<CustomerComplaint> semuaKomplain = complaintRepository.findAll();
            Sheet sheet2 = workbook.createSheet("Komplain Customer");

            CellStyle headerStyle2 = workbook.createCellStyle();
            headerStyle2.cloneStyleFrom(headerStyle1);
            headerStyle2.setFillForegroundColor(IndexedColors.ROSE.getIndex());

            Row headerRow2 = sheet2.createRow(0);
            String[] columns2 = {"ID", "Tanggal", "Customer", "Part Number", "Nama Part", "Jenis Cacat", "Quantity", "Lot Number", "Deskripsi Masalah"};

            for (int i = 0; i < columns2.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(columns2[i]);
                cell.setCellStyle(headerStyle2);
            }

            int rowIdx2 = 1;
            for (CustomerComplaint data : semuaKomplain) {
                Row row = sheet2.createRow(rowIdx2++);
                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getComplaintDate() != null ? data.getComplaintDate().toString() : "");
                row.createCell(2).setCellValue(data.getCustomerName());
                row.createCell(3).setCellValue(data.getPart().getPartNumber());
                row.createCell(4).setCellValue(data.getPart().getPartName());
                row.createCell(5).setCellValue(data.getDefectType().getName());
                row.createCell(6).setCellValue(data.getDefectQuantity());
                row.createCell(7).setCellValue(data.getLotNumber());
                row.createCell(8).setCellValue(data.getProblemDescription() != null ? data.getProblemDescription() : "-");
            }
            for (int i = 0; i < columns2.length; i++) sheet2.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat file Excel NG Data: " + e.getMessage());
        }
    }
    // 1. Produksi - CSV
    public String generateProductionCsv() {
        List<Production> semuaData = productionRepository.findAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Tanggal,Part Number,Nama Part,Target Qty,Produced Qty,Status\n");

        for (Production data : semuaData) {
            String status = (data.getProducedQuantity() >= data.getTargetQuantity()) ? "On Target" : "Below Target";
            csvBuilder.append(data.getId()).append(",")
                    .append(data.getProductionDate()).append(",")
                    .append(data.getPart().getPartNumber()).append(",")
                    .append(data.getPart().getPartName()).append(",")
                    .append(data.getTargetQuantity()).append(",")
                    .append(data.getProducedQuantity()).append(",")
                    .append(status)
                    .append("\n");
        }
        return csvBuilder.toString();
    }

    // 2. Produksi - PDF
    public byte[] generateProductionPdf() {
        try {
            List<Production> semuaData = productionRepository.findAll();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontJudul = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph judul = new Paragraph("Laporan Harian Hasil Produksi", fontJudul);
            judul.setAlignment(Element.ALIGN_CENTER);
            document.add(judul);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2.5f, 3f, 1.5f, 1.5f, 2f});

            String[] headers = {"ID", "Tanggal", "Part Number", "Nama Part", "Target", "Aktual", "Status"};
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String headerTitle : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(headerTitle, fontHeader));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Production data : semuaData) {
                String status = (data.getProducedQuantity() >= data.getTargetQuantity()) ? "On Target" : "Below Target";

                table.addCell(new PdfPCell(new Phrase(String.valueOf(data.getId()), fontData)));
                table.addCell(new PdfPCell(new Phrase(data.getProductionDate() != null ? data.getProductionDate().toString() : "-", fontData)));
                table.addCell(new PdfPCell(new Phrase(data.getPart().getPartNumber(), fontData)));
                table.addCell(new PdfPCell(new Phrase(data.getPart().getPartName(), fontData)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(data.getTargetQuantity()), fontData)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(data.getProducedQuantity()), fontData)));
                table.addCell(new PdfPCell(new Phrase(status, fontData)));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat file PDF Produksi: " + e.getMessage());
        }
    }

    // 3. Produksi - EXCEL
    public byte[] generateProductionExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Production> semuaData = productionRepository.findAll();
            Sheet sheet = workbook.createSheet("Data Produksi");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            excelFont.setBold(true);
            headerStyle.setFont(excelFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tanggal", "Part Number", "Nama Part", "Target Qty", "Produced Qty", "Status"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Production data : semuaData) {
                String status = (data.getProducedQuantity() >= data.getTargetQuantity()) ? "On Target" : "Below Target";

                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(data.getId());
                row.createCell(1).setCellValue(data.getProductionDate() != null ? data.getProductionDate().toString() : "");
                row.createCell(2).setCellValue(data.getPart().getPartNumber());
                row.createCell(3).setCellValue(data.getPart().getPartName());
                row.createCell(4).setCellValue(data.getTargetQuantity() != null ? data.getTargetQuantity() : 0);
                row.createCell(5).setCellValue(data.getProducedQuantity() != null ? data.getProducedQuantity() : 0);
                row.createCell(6).setCellValue(status);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat file Excel Produksi: " + e.getMessage());
        }
    }
}