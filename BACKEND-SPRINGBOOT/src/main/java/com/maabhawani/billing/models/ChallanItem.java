package com.maabhawani.billing.models;

import lombok.Data;

@Data
public class ChallanItem {
    private String challanNo;
    private String date;
    private String truckNo;
    private String hsnCode;
    private Double weight;
    private Double ratePerTr;
    private Double amount;
}
