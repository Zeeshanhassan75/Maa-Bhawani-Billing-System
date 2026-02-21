package com.maabhawani.billing.controllers;

import com.maabhawani.billing.models.Invoice;
import com.maabhawani.billing.services.ExcelGeneratorService;
import com.maabhawani.billing.services.InvoiceService;
import com.maabhawani.billing.services.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @Autowired
    private PdfGeneratorService pdfService;

    @Autowired
    private ExcelGeneratorService excelService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(service.saveInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(service.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) {
        return service.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String id) {
        return service.getInvoiceById(id).map(invoice -> {
            try {
                byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("filename", "invoice-" + invoice.getInvoiceNo() + ".pdf");
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<byte[]>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable String id) {
        return service.getInvoiceById(id).map(invoice -> {
            try {
                byte[] excelBytes = excelService.generateInvoiceExcel(invoice);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("filename", "invoice-" + invoice.getInvoiceNo() + ".xlsx");
                return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<byte[]>build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
