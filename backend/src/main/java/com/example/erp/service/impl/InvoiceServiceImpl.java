package com.example.erp.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.InvoiceRequestDTO;
import com.example.erp.dto.InvoiceResponseDTO;
import com.example.erp.dto.InvoiceItemDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.InvoiceItem;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.InvoiceItemRepository;
import com.example.erp.service.InvoiceService;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceItemRepository itemRepository) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {

        Invoice invoice = new Invoice();
        invoice.setCustomerId(dto.getCustomerId());
        invoice.setPaymentMethod(dto.getPaymentMethod());
        invoice.setStatus("PENDING");
        invoice.setCode("INV-" + System.currentTimeMillis());

        // Tính tổng tiền
        BigDecimal total = dto.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(total);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Lưu từng item
        List<InvoiceItem> items = dto.getItems().stream().map(i -> {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(savedInvoice);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());

        itemRepository.saveAll(items);

        return toResponseDTO(savedInvoice, items);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        List<InvoiceItem> items = itemRepository.findByInvoiceId(id);
        return toResponseDTO(invoice, items);
    }

    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(i -> {
                    List<InvoiceItem> items = itemRepository.findByInvoiceId(i.getId());
                    return toResponseDTO(i, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setCustomerId(dto.getCustomerId());
        invoice.setPaymentMethod(dto.getPaymentMethod());

        // Cập nhật items
        itemRepository.deleteAll(itemRepository.findByInvoiceId(id));

        List<InvoiceItem> items = dto.getItems().stream().map(i -> {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductId(i.getProductId());
            item.setQuantity(i.getQuantity());
            item.setPrice(i.getPrice());
            return item;
        }).collect(Collectors.toList());
        itemRepository.saveAll(items);

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        invoice.setTotalAmount(total);

        invoiceRepository.save(invoice);
        return toResponseDTO(invoice, items);
    }

    @Override
    public void deleteInvoice(Long id) {
        itemRepository.deleteAll(itemRepository.findByInvoiceId(id));
        invoiceRepository.deleteById(id);
    }

    @Override
    public void markAsPaid(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);
    }

    private InvoiceResponseDTO toResponseDTO(Invoice invoice, List<InvoiceItem> items) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setCode(invoice.getCode());
        dto.setCustomerId(invoice.getCustomerId());
        dto.setCreatedDate(invoice.getCreatedDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentMethod(invoice.getPaymentMethod());
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
