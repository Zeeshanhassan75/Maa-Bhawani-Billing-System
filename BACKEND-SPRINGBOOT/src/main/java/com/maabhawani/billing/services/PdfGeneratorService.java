package com.maabhawani.billing.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.maabhawani.billing.models.ChallanItem;
import com.maabhawani.billing.models.Invoice;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generateInvoicePdf(Invoice invoice) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Load Calibri fonts from classpath with fallback
        BaseFont calibriBase;
        BaseFont calibriBoldBase;
        try {
            calibriBase = BaseFont.createFont("/fonts/calibri.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            calibriBoldBase = BaseFont.createFont("/fonts/calibrib.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to standard Helvetica if TTF fonts are missing in container
            calibriBase = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            calibriBoldBase = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        }

        Font normalFont = new Font(calibriBase, 10, Font.NORMAL);
        Font boldFont = new Font(calibriBoldBase, 10, Font.BOLD);
        Font titleFont = new Font(calibriBoldBase, 16, Font.BOLD, java.awt.Color.BLUE);

        // Header Table
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[] { 1.5f, 1f, 1.5f });

        addCell(headerTable, getLabel(invoice, "headerMobileEmail", "Mob-9934115761\nE mail -Rajan20f@gmail.com"),
                boldFont, Element.ALIGN_LEFT);
        addCell(headerTable, getLabel(invoice, "headerTaxInvoice", "TAX INVOICE"), titleFont, Element.ALIGN_CENTER);
        addCell(headerTable,
                getLabel(invoice, "headerCopies",
                        "Original for Buyer\nDuplicate for Transporter\nTriplicate for Supporter"),
                normalFont,
                Element.ALIGN_RIGHT);

        document.add(headerTable);

        // Company Title
        PdfPTable companyTable = new PdfPTable(1);
        companyTable.setWidthPercentage(100);
        PdfPCell companyCell = new PdfPCell(
                new Phrase(getLabel(invoice, "companyName", "MAA BHAWANI TRADERS"),
                        new Font(calibriBoldBase, 18, Font.BOLD, java.awt.Color.BLUE)));
        companyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        companyCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
        companyTable.addCell(companyCell);

        PdfPCell addressCell = new PdfPCell(
                new Phrase(getLabel(invoice, "companyAddress",
                        "Near Bijli Office,BDO Road Gomia,P.O- IE Gomia Dist-Bokaro"), boldFont));
        addressCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        addressCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
        companyTable.addCell(addressCell);

        PdfPCell gstinCell = new PdfPCell(
                new Phrase(
                        getLabel(invoice, "companyGstin",
                                "GSTIN/UIN: 20AOFPY3578Q1Z5 PAN number : AOFPY3578Q State Name : Jharkhand, Code : 20"),
                        boldFont));
        gstinCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        gstinCell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        companyTable.addCell(gstinCell);

        // PO NO
        PdfPCell poCell = new PdfPCell(new Phrase(getLabel(invoice, "companyPo", "PO NO.4500777226"), boldFont));
        poCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        companyTable.addCell(poCell);

        document.add(companyTable);

        // Consignee & Invoice Details Matrix
        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[] { 1.5f, 1f });

        PdfPCell consigneeCell = new PdfPCell();
        consigneeCell.addElement(
                new Phrase(getLabel(invoice, "consigneeTitle", "Name & Address of the Consignee:"), boldFont));
        consigneeCell.addElement(
                new Phrase(invoice.getConsigneeName() + "\n" + invoice.getConsigneeAddress() + "\nGSTIN/UIN :: "
                        + invoice.getConsigneeGstin() + " Code : " + invoice.getConsigneeCode(), normalFont));
        detailsTable.addCell(consigneeCell);

        PdfPCell invoiceDetailsCell = new PdfPCell();
        invoiceDetailsCell.addElement(
                new Phrase(getLabel(invoice, "invoiceTitle", "Invoice No.: ") + invoice.getInvoiceNo(), boldFont));
        invoiceDetailsCell
                .addElement(new Phrase(getLabel(invoice, "invoiceModeTitle", "Mode of Transportation: ")
                        + invoice.getModeOfTransport(), normalFont));
        invoiceDetailsCell
                .addElement(new Phrase(getLabel(invoice, "invoiceDateTitle", "Date: ") + invoice.getDate(), boldFont));
        detailsTable.addCell(invoiceDetailsCell);

        // From / To Locations
        PdfPCell fromCell = new PdfPCell(
                new Phrase(getLabel(invoice, "fromTitle", "FROM: ") + invoice.getFromLocation(), boldFont));
        detailsTable.addCell(fromCell);
        PdfPCell toCell = new PdfPCell(
                new Phrase(getLabel(invoice, "toTitle", "TO: ") + invoice.getToLocation(), boldFont));
        detailsTable.addCell(toCell);

        document.add(detailsTable);

        // Items Table
        PdfPTable itemsTable = new PdfPTable(7);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[] { 0.8f, 1f, 1.2f, 0.8f, 0.8f, 1f, 1.2f });

        String[] headers = { "CHALLAN NO", "DATE", "TRUCK NO.", "HSN Code", "Weight", "RATE PER TR", "AMOUNT (Rs.)" };
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, boldFont));
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(hCell);
        }

        List<ChallanItem> items = invoice.getChallanItems();
        if (items != null) {
            for (ChallanItem item : items) {
                addCellCenter(itemsTable, item.getChallanNo(), normalFont);
                addCellCenter(itemsTable, item.getDate(), normalFont);
                addCellCenter(itemsTable, item.getTruckNo(), normalFont);
                addCellCenter(itemsTable, item.getHsnCode(), normalFont);
                addCellCenter(itemsTable, String.valueOf(item.getWeight()), normalFont);
                addCellCenter(itemsTable, String.valueOf(item.getRatePerTr()), normalFont);
                addCellCenter(itemsTable, String.valueOf(item.getAmount()), normalFont);
            }
        }

        // Pad empty rows for spacing if needed here (Skipped for simplicity, but can
        // Pad empty rows for spacing if needed here (Skipped for simplicity, but can
        // add blank rows)

        PdfPCell bankCell = new PdfPCell();
        bankCell.setColspan(5);
        bankCell.addElement(new Phrase(getLabel(invoice, "bankDetailsTitle", "Bank Details:"), boldFont));
        bankCell.addElement(
                new Phrase("Bank Name: " + invoice.getBankName() + ", Branch: " + invoice.getBankBranch(), normalFont));
        bankCell.addElement(new Phrase(
                "Bank Account No.: " + invoice.getBankAccountNo() + " | IFSC Code: " + invoice.getBankIfscCode(),
                normalFont));
        itemsTable.addCell(bankCell);

        PdfPCell totalDescCell = new PdfPCell(
                new Phrase(getLabel(invoice, "totalInvoiceValueTitle", "Total Invoice Value:"), boldFont));
        totalDescCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        itemsTable.addCell(totalDescCell);

        PdfPCell totalValCell = new PdfPCell(new Phrase(String.valueOf(invoice.getTotalInvoiceValue()), boldFont));
        totalValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        itemsTable.addCell(totalValCell);

        document.add(itemsTable);

        // Words
        PdfPTable wordsTable = new PdfPTable(1);
        wordsTable.setWidthPercentage(100);
        addCell(wordsTable,
                getLabel(invoice, "invoiceValueInWordsTitle", "Invoice Value in Words: Rs. ")
                        + invoice.getInvoiceValueInWords(),
                boldFont,
                Element.ALIGN_LEFT);
        document.add(wordsTable);

        // Tax details layout
        PdfPTable taxTable = new PdfPTable(2);
        taxTable.setWidthPercentage(100);
        taxTable.setWidths(new float[] { 1.5f, 1f });

        PdfPCell taxLeft = new PdfPCell();
        taxLeft.addElement(new Phrase(
                getLabel(invoice, "creditInputText",
                        "Credit on input tax on goods and services used in supplying the services has not been taken."),
                boldFont));

        PdfPTable subTaxTable = new PdfPTable(2);
        subTaxTable.setWidthPercentage(100);
        addCellRight(subTaxTable, getLabel(invoice, "taxableValueTitle", "TAXABLE VALUE"), boldFont);
        addCellCenter(subTaxTable, String.valueOf(invoice.getTaxableValue()), normalFont);
        addCellRight(subTaxTable, getLabel(invoice, "grossFreightTitle", "GROSS FREIGHT:"), normalFont);
        addCellCenter(subTaxTable, String.valueOf(invoice.getGrossFreight()), normalFont);
        addCellRight(subTaxTable, getLabel(invoice, "igstTitle", "IGST @ ") + invoice.getIgstPercentage() + "%:",
                normalFont);
        addCellCenter(subTaxTable, String.valueOf(invoice.getIgstAmount()), normalFont);
        addCellRight(subTaxTable, getLabel(invoice, "taxPayableTitle", "TAX PAYABLE BY CONSIGNEE:"), boldFont);
        addCellCenter(subTaxTable, String.valueOf(invoice.getTaxPayable()), normalFont);
        taxLeft.addElement(subTaxTable);

        taxLeft.addElement(new Phrase(
                getLabel(invoice, "declarationTitle", "Declaration:") + "\n" + getLabel(invoice, "declarationText",
                        "We hereby certify that Cenvat credit on input services used for providing taxable service, has not been taken under the provisions on Cenvat Credit rules 2004.\nThe GST is to be paid under reverse charge mechanism by the recipient of service."),
                normalFont));
        taxTable.addCell(taxLeft);

        PdfPCell signatureCell = new PdfPCell();
        signatureCell
                .addElement(new Phrase(getLabel(invoice, "forCompanyTitle", "For: MAA BHAWANI TRADERS"), boldFont));
        signatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph emptySigSpace = new Paragraph("\n\n\n");
        signatureCell.addElement(emptySigSpace);

        Paragraph authSign = new Paragraph(getLabel(invoice, "authSignatoryTitle", "Authorised signatory"), boldFont);
        authSign.setAlignment(Element.ALIGN_RIGHT);
        signatureCell.addElement(authSign);

        taxTable.addCell(signatureCell);

        document.add(taxTable);
        document.close();

        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addCellCenter(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCellRight(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private String getLabel(Invoice invoice, String key, String defaultValue) {
        if (invoice.getCustomLabels() != null && invoice.getCustomLabels().containsKey(key)) {
            return invoice.getCustomLabels().get(key);
        }
        return defaultValue;
    }
}
