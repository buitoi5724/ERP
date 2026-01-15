package com.example.erp.controller;

import org.springframework.web.bind.annotation.*;
import com.example.erp.dto.InvoiceRequestDTO;
import com.example.erp.dto.InvoiceResponseDTO;
import com.example.erp.service.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public InvoiceResponseDTO create(@RequestBody InvoiceRequestDTO dto) {
        return invoiceService.createInvoice(dto);
    }

    @GetMapping("/{id}")
    public InvoiceResponseDTO getById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @GetMapping
    public List<InvoiceResponseDTO> getAll() {
        return invoiceService.getAllInvoices();
    }

    @PutMapping("/{id}")
    public InvoiceResponseDTO update(@PathVariable Long id, @RequestBody InvoiceRequestDTO dto) {
        return invoiceService.updateInvoice(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }

    @PostMapping("/{id}/pay")
    public void pay(@PathVariable Long id) {
        invoiceService.markAsPaid(id);
    }
}
