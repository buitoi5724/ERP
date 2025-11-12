package com.example.erp.service;

import com.example.erp.entity.Invoice;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired private InvoiceRepository invoiceRepo;

    public Invoice create(Invoice invoice) {
        invoice.setCode(CodeGenerator.generateCode("INV"));
        invoice.setCreatedDate(LocalDateTime.now());
        return invoiceRepo.save(invoice);
    }

    public List<Invoice> getAll() { return invoiceRepo.findAll(); }
}
