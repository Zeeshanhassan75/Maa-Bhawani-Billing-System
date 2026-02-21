package com.maabhawani.billing.services;

import com.maabhawani.billing.models.Invoice;
import com.maabhawani.billing.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository repository;

    public Invoice saveInvoice(Invoice invoice) {
        return repository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return repository.findAll();
    }

    public Optional<Invoice> getInvoiceById(String id) {
        return repository.findById(id);
    }
}
