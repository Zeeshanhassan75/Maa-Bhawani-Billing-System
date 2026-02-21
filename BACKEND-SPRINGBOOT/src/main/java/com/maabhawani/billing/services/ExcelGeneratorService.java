package com.maabhawani.billing.services;

import com.maabhawani.billing.models.ChallanItem;
import com.maabhawani.billing.models.Invoice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelGeneratorService {

    public byte[] generateInvoiceExcel(Invoice invoice) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Invoice");

            // Fonts
            Font boldFont = workbook.createFont();
            boldFont.setFontName("Calibri");
            boldFont.setBold(true);

            Font normalFont = workbook.createFont();
            normalFont.setFontName("Calibri");

            Font titleFont = workbook.createFont();
            titleFont.setFontName("Calibri");
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);

            // Styles
            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            boldStyle.setAlignment(HorizontalAlignment.LEFT);
            boldStyle.setBorderBottom(BorderStyle.THIN);
            boldStyle.setBorderTop(BorderStyle.THIN);
            boldStyle.setBorderRight(BorderStyle.THIN);
            boldStyle.setBorderLeft(BorderStyle.THIN);

            CellStyle normalStyle = workbook.createCellStyle();
            normalStyle.setFont(normalFont);
            normalStyle.setAlignment(HorizontalAlignment.CENTER);
            normalStyle.setBorderBottom(BorderStyle.THIN);
            normalStyle.setBorderTop(BorderStyle.THIN);
            normalStyle.setBorderRight(BorderStyle.THIN);
            normalStyle.setBorderLeft(BorderStyle.THIN);

            int rowNum = 0;

            // Basic structure output simplified for Excel tabular form

            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, "Invoice No:", boldStyle);
            createCell(row, 1, invoice.getInvoiceNo(), normalStyle);
            createCell(row, 2, "Date:", boldStyle);
            createCell(row, 3, invoice.getDate(), normalStyle);

            row = sheet.createRow(rowNum++);
            createCell(row, 0, "Consignee:", boldStyle);
            createCell(row, 1, invoice.getConsigneeName(), normalStyle);

            row = sheet.createRow(rowNum++);
            createCell(row, 0, "From:", boldStyle);
            createCell(row, 1, invoice.getFromLocation(), normalStyle);
            createCell(row, 2, "To:", boldStyle);
            createCell(row, 3, invoice.getToLocation(), normalStyle);

            rowNum++; // Empty row

            row = sheet.createRow(rowNum++);
            String[] headers = { "CHALLAN NO", "DATE", "TRUCK NO.", "HSN Code", "Weight", "RATE PER TR",
                    "AMOUNT (Rs.)" };
            for (int i = 0; i < headers.length; i++) {
                createCell(row, i, headers[i], boldStyle);
            }

            List<ChallanItem> items = invoice.getChallanItems();
            if (items != null) {
                for (ChallanItem item : items) {
                    row = sheet.createRow(rowNum++);
                    createCell(row, 0, item.getChallanNo(), normalStyle);
                    createCell(row, 1, item.getDate(), normalStyle);
                    createCell(row, 2, item.getTruckNo(), normalStyle);
                    createCell(row, 3, item.getHsnCode(), normalStyle);
                    if (item.getWeight() != null)
                        createCell(row, 4, item.getWeight().toString(), normalStyle);
                    if (item.getRatePerTr() != null)
                        createCell(row, 5, item.getRatePerTr().toString(), normalStyle);
                    if (item.getAmount() != null)
                        createCell(row, 6, item.getAmount().toString(), normalStyle);
                }
            }

            rowNum++;
            row = sheet.createRow(rowNum++);
            createCell(row, 5, "Total Value:", boldStyle);
            if (invoice.getTotalInvoiceValue() != null) {
                createCell(row, 6, invoice.getTotalInvoiceValue().toString(), boldStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createCell(Row row, int columnCount, String value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}
