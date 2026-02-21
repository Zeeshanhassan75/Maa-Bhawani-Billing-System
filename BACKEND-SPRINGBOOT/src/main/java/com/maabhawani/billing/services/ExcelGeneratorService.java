package com.maabhawani.billing.services;

import com.maabhawani.billing.models.ChallanItem;
import com.maabhawani.billing.models.Invoice;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelGeneratorService {

        public byte[] generateInvoiceExcel(Invoice invoice) throws Exception {
                try (Workbook workbook = new XSSFWorkbook()) {
                        Sheet sheet = workbook.createSheet("Invoice");
                        // Page margins
                        sheet.setMargin(Sheet.TopMargin, 0.5d);
                        sheet.setMargin(Sheet.BottomMargin, 0.5d);
                        sheet.setMargin(Sheet.LeftMargin, 0.25d);
                        sheet.setMargin(Sheet.RightMargin, 0.25d);

                        // Page Setup for fitting into A4
                        PrintSetup printSetup = sheet.getPrintSetup();
                        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
                        printSetup.setLandscape(false);
                        printSetup.setFitHeight((short) 1);
                        printSetup.setFitWidth((short) 1);
                        sheet.setAutobreaks(true);
                        sheet.setFitToPage(true);

                        // Set column widths matching table layout
                        sheet.setColumnWidth(0, 4000); // CHALLAN NO
                        sheet.setColumnWidth(1, 3500); // DATE
                        sheet.setColumnWidth(2, 4500); // TRUCK NO
                        sheet.setColumnWidth(3, 3500); // HSN Code
                        sheet.setColumnWidth(4, 3000); // Weight
                        sheet.setColumnWidth(5, 5000); // RATE PER TR
                        sheet.setColumnWidth(6, 4500); // AMOUNT

                        // Fonts
                        Font normalFont = workbook.createFont();
                        normalFont.setFontName("SansSerif");
                        normalFont.setFontHeightInPoints((short) 10);

                        Font boldFont = workbook.createFont();
                        boldFont.setFontName("SansSerif");
                        boldFont.setFontHeightInPoints((short) 10);
                        boldFont.setBold(true);

                        Font titleFont = workbook.createFont();
                        titleFont.setFontName("SansSerif");
                        titleFont.setFontHeightInPoints((short) 14);
                        titleFont.setBold(true);

                        Font companyFont = workbook.createFont();
                        companyFont.setFontName("SansSerif");
                        companyFont.setFontHeightInPoints((short) 18);
                        companyFont.setBold(true);

                        // Styles
                        CellStyle normalCenterStyle = createStyle(workbook, normalFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.TOP, false, false);
                        CellStyle normalRightStyle = createStyle(workbook, normalFont, HorizontalAlignment.RIGHT,
                                        VerticalAlignment.TOP, false, false);

                        CellStyle boldCenterStyle = createStyle(workbook, boldFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.TOP, false, false);
                        CellStyle boldRightStyle = createStyle(workbook, boldFont, HorizontalAlignment.RIGHT,
                                        VerticalAlignment.TOP,
                                        false, false);

                        CellStyle titleStyle = createStyle(workbook, titleFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.CENTER, false, false);
                        CellStyle companyStyle = createStyle(workbook, companyFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.CENTER, false, false);

                        int rowNum = 0;
                        Map<String, String> labels = invoice.getCustomLabels();

                        // 1. Top Header Row (Mobile/Email | TAX INVOICE | Copy Info)
                        Row row0 = sheet.createRow(rowNum);
                        row0.setHeightInPoints(40);
                        String contactInfo = (labels != null && labels.containsKey("headerMobileEmail"))
                                        ? labels.get("headerMobileEmail")
                                        : "Mob-9934115761\nE mail -Rajan20f@gmail.com";
                        createCell(row0, 0, contactInfo,
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT, VerticalAlignment.TOP,
                                                        true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));

                        String title = (labels != null && labels.containsKey("headerTaxInvoice"))
                                        ? labels.get("headerTaxInvoice")
                                        : "TAX INVOICE";
                        createCell(row0, 2, title, titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, 4));

                        String copyInfo = "Original for Buyer\nDuplicate for Transporter\nTriplicate for Supporter";
                        createCell(row0, 5, copyInfo,
                                        createStyle(workbook, normalFont, HorizontalAlignment.RIGHT,
                                                        VerticalAlignment.TOP, true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 5, 6));

                        rowNum++;

                        // 2. Company Info Block
                        // MAA BHAWANI TRADERS
                        Row row1 = sheet.createRow(rowNum);
                        row1.setHeightInPoints(25);
                        String companyName = (labels != null && labels.containsKey("companyName"))
                                        ? labels.get("companyName")
                                        : "MAA BHAWANI TRADERS";
                        createCell(row1, 0, companyName, companyStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
                        setRegionBorder(new CellRangeAddress(rowNum, rowNum, 0, 6), sheet);
                        rowNum++;

                        // Company Address
                        Row row2 = sheet.createRow(rowNum);
                        String address = (labels != null && labels.containsKey("companyAddress"))
                                        ? labels.get("companyAddress")
                                        : "Near Bijli Office, BDO Road Gomia, P.O- IE Gomia Dist-Bokaro";
                        createCell(row2, 0, address, boldCenterStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
                        rowNum++;

                        // GSTIN
                        Row row3 = sheet.createRow(rowNum);
                        String gstin = (labels != null && labels.containsKey("companyGstin"))
                                        ? labels.get("companyGstin")
                                        : "GSTIN/UIN: 20AOFPY3578Q1Z5 PAN number : AOFPY3578Q State Name : Jharkhand, Code : 20";
                        createCell(row3, 0, gstin, boldCenterStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
                        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 6), sheet);
                        rowNum++;

                        // PO NO
                        Row row4 = sheet.createRow(rowNum);
                        String poNo = (labels != null && labels.containsKey("companyPo")) ? labels.get("companyPo")
                                        : "PO NO. 4500777226";
                        createCell(row4, 0, poNo, boldRightStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
                        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 6), sheet);

                        // Put outer border for entire company block (row 1 to 4)
                        setRegionBorder(new CellRangeAddress(1, 4, 0, 6), sheet);
                        rowNum++;

                        // 3. Consignee & Invoice Details Block
                        int consigneeStartRow = rowNum;
                        Row row5 = sheet.createRow(rowNum);
                        row5.setHeightInPoints(60);

                        String consigneeText = "Name & Address of the Consignee:\n" +
                                        emptyIfNull(invoice.getConsigneeName()) + "\n" +
                                        emptyIfNull(invoice.getConsigneeAddress()) + "\n" +
                                        "GSTIN/UIN :: " + emptyIfNull(invoice.getConsigneeGstin()) + " Code : "
                                        + emptyIfNull(invoice.getConsigneeCode());
                        createCell(row5, 0, consigneeText,
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT, VerticalAlignment.TOP,
                                                        true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
                        RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 3), sheet);

                        String invoiceMeta = "Invoice No.: " + emptyIfNull(invoice.getInvoiceNo()) + "\n" +
                                        "Mode of Transportation: " + emptyIfNull(invoice.getModeOfTransport()) + "\n" +
                                        "Date: " + emptyIfNull(invoice.getDate());
                        createCell(row5, 4, invoiceMeta,
                                        createStyle(workbook, normalFont, HorizontalAlignment.LEFT,
                                                        VerticalAlignment.TOP, true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 6));
                        rowNum++;

                        // FROM / TO
                        Row row6 = sheet.createRow(rowNum);
                        createCell(row6, 0, "FROM: " + emptyIfNull(invoice.getFromLocation()),
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT, VerticalAlignment.TOP,
                                                        true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
                        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 3), sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 3), sheet);

                        createCell(row6, 4, "TO: " + emptyIfNull(invoice.getToLocation()),
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT, VerticalAlignment.TOP,
                                                        true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 6));
                        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 6), sheet);
                        rowNum++;

                        // Outer border for Consignee block
                        setRegionBorder(new CellRangeAddress(consigneeStartRow, rowNum - 1, 0, 6), sheet);

                        // 4. Items Table
                        int tableStartRow = rowNum;
                        Row headerRow = sheet.createRow(rowNum++);
                        String[] headers = { "CHALLAN NO", "DATE", "TRUCK NO.", "HSN Code", "Weight", "RATE PER TR",
                                        "AMOUNT (Rs.)" };

                        CellStyle headerNoTopBorder = createStyle(workbook, boldFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.CENTER, true, true);
                        headerNoTopBorder.setBorderTop(BorderStyle.NONE);

                        for (int i = 0; i < headers.length; i++) {
                                createCell(headerRow, i, headers[i], headerNoTopBorder);
                        }

                        CellStyle dataNoTB_Border = createStyle(workbook, normalFont, HorizontalAlignment.CENTER,
                                        VerticalAlignment.CENTER, false, true);

                        List<ChallanItem> items = invoice.getChallanItems();
                        if (items != null) {
                                for (ChallanItem item : items) {
                                        Row itemRow = sheet.createRow(rowNum++);
                                        createCell(itemRow, 0, item.getChallanNo(), dataNoTB_Border);
                                        createCell(itemRow, 1, item.getDate(), dataNoTB_Border);
                                        createCell(itemRow, 2, item.getTruckNo(), dataNoTB_Border);
                                        createCell(itemRow, 3, item.getHsnCode(), dataNoTB_Border);
                                        createCell(itemRow, 4,
                                                        item.getWeight() != null ? item.getWeight().toString() : "",
                                                        dataNoTB_Border);
                                        createCell(itemRow, 5,
                                                        item.getRatePerTr() != null ? item.getRatePerTr().toString()
                                                                        : "",
                                                        dataNoTB_Border);
                                        createCell(itemRow, 6,
                                                        item.getAmount() != null ? item.getAmount().toString() : "",
                                                        dataNoTB_Border);
                                }
                        }

                        // 5. Totals & Bank Details Row
                        Row bankRow = sheet.createRow(rowNum);
                        bankRow.setHeightInPoints(45);

                        Font underlineBoldFont = workbook.createFont();
                        underlineBoldFont.setFontName("SansSerif");
                        underlineBoldFont.setFontHeightInPoints((short) 10);
                        underlineBoldFont.setBold(true);
                        underlineBoldFont.setUnderline(Font.U_SINGLE);

                        RichTextString rts = workbook.getCreationHelper()
                                        .createRichTextString("Bank Details:\nBank Name: "
                                                        + emptyIfNull(invoice.getBankName())
                                                        + ", Branch: "
                                                        + emptyIfNull(invoice.getBankBranch()) + "\n" +
                                                        "Bank Account No.: " + emptyIfNull(invoice.getBankAccountNo())
                                                        + " | IFSC Code: "
                                                        + emptyIfNull(invoice.getBankIfscCode()));

                        rts.applyFont(0, 13, underlineBoldFont);

                        CellStyle bankStyle = createStyle(workbook, normalFont, HorizontalAlignment.LEFT,
                                        VerticalAlignment.TOP, true, true);
                        bankStyle.setBorderTop(BorderStyle.THIN);
                        bankStyle.setBorderBottom(BorderStyle.NONE);

                        Cell bankC = bankRow.createCell(0);
                        bankC.setCellValue(rts);
                        bankC.setCellStyle(bankStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
                        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 3), sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 0, 3), sheet);

                        createCell(bankRow, 4, "Total Invoice Value:",
                                        createStyle(workbook, boldFont, HorizontalAlignment.RIGHT,
                                                        VerticalAlignment.BOTTOM, true, true));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 5));
                        RegionUtil.setBorderTop(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 5), sheet);

                        createCell(bankRow, 6,
                                        invoice.getTotalInvoiceValue() != null
                                                        ? invoice.getTotalInvoiceValue().toString()
                                                        : "",
                                        createStyle(workbook, boldFont, HorizontalAlignment.CENTER,
                                                        VerticalAlignment.BOTTOM, false, true));

                        setRegionBorder(new CellRangeAddress(tableStartRow, rowNum, 0, 6), sheet);
                        rowNum++;

                        // 6. Value in words
                        Row wordsRow = sheet.createRow(rowNum);
                        createCell(wordsRow, 0,
                                        "Invoice Value in Words: " + emptyIfNull(invoice.getInvoiceValueInWords()),
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT,
                                                        VerticalAlignment.CENTER, false, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));
                        setRegionBorder(new CellRangeAddress(rowNum, rowNum, 0, 6), sheet);
                        rowNum++;

                        // 7. Declaration & Tax Table
                        int decStartRow = rowNum;
                        Row decRow1 = sheet.createRow(rowNum);
                        String mainDec = "Credit on input tax on goods and services used in supplying the services has not been taken.";
                        createCell(decRow1, 0, mainDec,
                                        createStyle(workbook, boldFont, HorizontalAlignment.LEFT, VerticalAlignment.TOP,
                                                        true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + 3, 0, 3));
                        RegionUtil.setBorderRight(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum + 3, 0, 3),
                                        sheet);

                        // Taxable Value
                        createCell(decRow1, 4, "TAXABLE VALUE", boldRightStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 5));
                        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 5), sheet);
                        createCell(decRow1, 6,
                                        invoice.getTaxableValue() != null ? invoice.getTaxableValue().toString()
                                                        : "0.0",
                                        normalCenterStyle);
                        rowNum++;

                        // Gross Freight
                        Row decRow2 = sheet.createRow(rowNum);
                        createCell(decRow2, 4, "GROSS FREIGHT:", normalRightStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 5));
                        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 5), sheet);
                        createCell(decRow2, 6,
                                        invoice.getGrossFreight() != null ? invoice.getGrossFreight().toString()
                                                        : "0.0",
                                        normalCenterStyle);
                        rowNum++;

                        // IGST
                        Row decRow3 = sheet.createRow(rowNum);
                        String igstText = "IGST @ "
                                        + (invoice.getIgstPercentage() != null ? invoice.getIgstPercentage() : "")
                                        + "%:";
                        createCell(decRow3, 4, igstText, normalRightStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 5));
                        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 5), sheet);
                        createCell(decRow3, 6,
                                        invoice.getIgstAmount() != null ? invoice.getIgstAmount().toString() : "0.0",
                                        normalCenterStyle);
                        rowNum++;

                        // Tax Payable
                        Row decRow4 = sheet.createRow(rowNum);
                        createCell(decRow4, 4, "TAX PAYABLE BY CONSIGNEE:", boldRightStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 5));
                        RegionUtil.setBorderLeft(BorderStyle.THIN, new CellRangeAddress(rowNum, rowNum, 4, 5), sheet);
                        createCell(decRow4, 6,
                                        invoice.getTaxPayable() != null ? invoice.getTaxPayable().toString() : "0.0",
                                        normalCenterStyle);

                        setRegionBorder(new CellRangeAddress(decStartRow, rowNum, 0, 6), sheet);
                        rowNum++;

                        // 8. Signatory row
                        Row sigRow = sheet.createRow(rowNum);
                        sigRow.setHeightInPoints(100);

                        String declaration2 = "Declaration:\n" +
                                        "We hereby certify that Cenvat credit on input services used for providing taxable\nservice, has not been taken under the provisions on Cenvat Credit rules 2004.\n"
                                        +
                                        "The GST is to be paid under reverse charge mechanism by the recipient\nof service.";

                        RichTextString dec2Rts = workbook.getCreationHelper().createRichTextString(declaration2);
                        dec2Rts.applyFont(0, 12, boldFont);

                        Cell decC = sigRow.createCell(0);
                        decC.setCellValue(dec2Rts);
                        decC.setCellStyle(createStyle(workbook, normalFont, HorizontalAlignment.LEFT,
                                        VerticalAlignment.TOP, true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));

                        String forCompany = (labels != null && labels.containsKey("forCompanyTitle"))
                                        ? labels.get("forCompanyTitle")
                                        : "For: MAA BHAWANI TRADERS";
                        String authSig = (labels != null && labels.containsKey("authSignatoryTitle"))
                                        ? labels.get("authSignatoryTitle")
                                        : "Authorised signatory";

                        String sigText = forCompany + "\n\n\n\n\n\n" + authSig;
                        createCell(sigRow, 4, sigText,
                                        createStyle(workbook, boldFont, HorizontalAlignment.RIGHT,
                                                        VerticalAlignment.TOP, true, false));
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 6));

                        // Final outer border
                        setRegionBorder(new CellRangeAddress(1, rowNum, 0, 6), sheet);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        workbook.write(outputStream);
                        return outputStream.toByteArray();
                }
        }

        private Cell createCell(Row row, int column, String value, CellStyle style) {
                Cell cell = row.createCell(column);
                cell.setCellValue(value != null ? value : "");
                if (style != null) {
                        cell.setCellStyle(style);
                }
                return cell;
        }

        private CellStyle createStyle(Workbook workbook, Font font, HorizontalAlignment hAlign,
                        VerticalAlignment vAlign,
                        boolean wrapText, boolean borders) {
                CellStyle style = workbook.createCellStyle();
                style.setFont(font);
                style.setAlignment(hAlign);
                style.setVerticalAlignment(vAlign);
                style.setWrapText(wrapText);
                if (borders) {
                        style.setBorderTop(BorderStyle.THIN);
                        style.setBorderBottom(BorderStyle.THIN);
                        style.setBorderLeft(BorderStyle.THIN);
                        style.setBorderRight(BorderStyle.THIN);
                }
                return style;
        }

        private void setRegionBorder(CellRangeAddress region, Sheet sheet) {
                RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
                RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        }

        private String emptyIfNull(String val) {
                return val == null ? "" : val;
        }
}
