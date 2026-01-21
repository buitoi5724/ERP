package com.example.erp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.InvoiceItemDTO;
import com.example.erp.dto.InvoiceRequestDTO;
import com.example.erp.dto.InvoiceResponseDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.InvoiceItem;
import com.example.erp.repository.InvoiceItemRepository;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.service.InvoiceService;
import com.example.erp.util.InvoiceStatus;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            InvoiceItemRepository itemRepository) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
    }

    // ================= CREATE =================
    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one item");
        }

        Invoice invoice = new Invoice();
        invoice.setCode("INV-" + System.currentTimeMillis());
        invoice.setPartnerId(dto.getPartnerId());
        invoice.setType(dto.getType());
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setStatus(InvoiceStatus.DRAFT);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<InvoiceItem> items = dto.getItems().stream().map(i -> {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());

        itemRepository.saveAll(items);

        savedInvoice.setTotalAmount(calculateTotal(items));
        invoiceRepository.save(savedInvoice);

        return toResponseDTO(savedInvoice, items);
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        List<InvoiceItem> items = itemRepository.findByInvoiceId(id);
        return toResponseDTO(invoice, items);
    }

    // ================= GET ALL =================
    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {

        return invoiceRepository.findAll().stream()
                .map(inv -> {
                    List<InvoiceItem> items =
                            itemRepository.findByInvoiceId(inv.getId());
                    return toResponseDTO(inv, items);
                })
                .collect(Collectors.toList());
    }

    // ================= UPDATE =================
    @Override
    public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Paid invoice cannot be updated");
        }

        invoice.setPartnerId(dto.getPartnerId());
        invoice.setType(dto.getType());
        invoice.setPaymentMethod(dto.getPaymentMethod());

        itemRepository.deleteAll(
                itemRepository.findByInvoiceId(id)
        );

        List<InvoiceItem> items = dto.getItems().stream().map(i -> {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());

        itemRepository.saveAll(items);

        invoice.setTotalAmount(calculateTotal(items));
        invoiceRepository.save(invoice);

        return toResponseDTO(invoice, items);
    }

    // ================= DELETE =================
    @Override
    public void deleteInvoice(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot delete paid invoice");
        }

        itemRepository.deleteAll(
                itemRepository.findByInvoiceId(id)
        );
        invoiceRepository.delete(invoice);
    }

    // ================= MARK PAID =================
    @Override
    public void markAsPaid(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    // ================= HELPERS =================
    private BigDecimal calculateTotal(List<InvoiceItem> items) {
        return items.stream()
                .map(i -> i.getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private InvoiceResponseDTO toResponseDTO(
            Invoice invoice,
            List<InvoiceItem> items) {

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setCode(invoice.getCode());

        // 🔥 QUAN TRỌNG
        dto.setPartnerId(invoice.getPartnerId());
        dto.setType(invoice.getType());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentMethod(invoice.getPaymentMethod());

        dto.setCreatedDate(invoice.getCreatedDate());
        dto.setTotalAmount(invoice.getTotalAmount());

        dto.setItems(items.stream().map(i -> {
            InvoiceItemDTO itemDTO = new InvoiceItemDTO();
            itemDTO.setProductId(i.getProductId());
            itemDTO.setQuantity(i.getQuantity());
            itemDTO.setPrice(i.getPrice());
            return itemDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
}
