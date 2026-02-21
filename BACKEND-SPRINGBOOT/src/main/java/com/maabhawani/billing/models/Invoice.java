package com.maabhawani.billing.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "invoices")
public class Invoice {
    @Id
    private String id;

    // Header
    private String invoiceNo;
    private String date;
    private String modeOfTransport;

    // Consignee Details
    private String consigneeName;
    private String consigneeAddress;
    private String consigneeGstin;
    private String consigneeCode;

    // Locations
    private String fromLocation;
    private String toLocation;

    // Items
    private List<ChallanItem> challanItems;

    // Bank Details
    private String bankName;
    private String bankBranch;
    private String bankAccountNo;
    private String bankIfscCode;

    // Totals
    private Double totalInvoiceValue;
    private String invoiceValueInWords;
    private Double taxableValue;
    private Double grossFreight;
    private Double igstPercentage;
    private Double igstAmount;
    private Double taxPayable;

    // Custom Editable Labels
    private Map<String, String> customLabels;
}
