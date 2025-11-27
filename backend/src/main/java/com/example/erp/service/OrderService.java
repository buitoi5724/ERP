package com.example.erp.service;

import com.example.erp.dto.InvoiceDTO;
import com.example.erp.dto.InventoryDTO;
import com.example.erp.dto.OrderDTO;
import com.example.erp.dto.OrderItemDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.Order;
import com.example.erp.entity.OrderItem;
import com.example.erp.entity.Product;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.OrderRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    @Transactional
    public Order createOrder(Order order) {
        order.setCode(CodeGenerator.generateCode("ORD"));
        order.setOrderDate(LocalDateTime.now());

        double subtotal = 0.0;

        // Gán thông tin cho từng item
        for (OrderItem item : order.getItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("productId bị thiếu.");
            }

            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + item.getProductId()));

            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setOrder(order);  // quan trọng: liên kết item với order
            subtotal += item.getPrice() * item.getQuantity();
        }

        order.setSubtotal(subtotal);
        Order savedOrder = orderRepo.save(order);

        // === Tạo invoice liên kết với order ===
        Invoice invoice = new Invoice();
        invoice.setOrder(savedOrder);
        invoice.setCode(CodeGenerator.generateCode("INV"));
        invoice.setCreatedDate(LocalDateTime.now());
        invoice.setTotalAmount(subtotal
                + (order.getTax() != null ? order.getTax() : 0)
                + (order.getShippingFee() != null ? order.getShippingFee() : 0)
                - (order.getDiscount() != null ? order.getDiscount() : 0));
        invoice.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : "unknown");

        invoiceRepo.save(invoice);
        savedOrder.addInvoice(invoice);

        return savedOrder;
    }

    // === Convert Order -> DTO ===
    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getCode());
        dto.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : null);
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setPhone(order.getPhone());
        dto.setNote(order.getNote());
        dto.setSubtotal(order.getSubtotal());
        dto.setTax(order.getTax());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        double total =
                (order.getSubtotal() != null ? order.getSubtotal() : 0)
                        + (order.getTax() != null ? order.getTax() : 0)
                        + (order.getShippingFee() != null ? order.getShippingFee() : 0)
                        - (order.getDiscount() != null ? order.getDiscount() : 0);
        dto.setTotalAmount(total);
        dto.setPaymentMethod(order.getPaymentMethod());

        // Map order items
        List<OrderItemDTO> itemsDTO = order.getItems().stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setProductId(item.getProductId());
            i.setProductName(item.getProductName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            return i;
        }).collect(Collectors.toList());
        dto.setItems(itemsDTO);

        // Map invoice
        if (order.getInvoices() != null && !order.getInvoices().isEmpty()) {
            Invoice invoice = order.getInvoices().get(0);
            InvoiceDTO invoiceDTO = new InvoiceDTO();
            invoiceDTO.setId(invoice.getId());
            invoiceDTO.setOrderId(order.getId());
            invoiceDTO.setOrderCode(order.getCode());
            invoiceDTO.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : null);
            invoiceDTO.setAmount(invoice.getTotalAmount());
            invoiceDTO.setPaymentMethod(invoice.getPaymentMethod());

            List<InventoryDTO> itemsInvoice = order.getItems().stream().map(item -> {
                InventoryDTO inv = new InventoryDTO();
                inv.setProductName(item.getProductName());
                inv.setQuantity(item.getQuantity());
                inv.setPrice(item.getPrice());
                inv.setOrderId(order.getId());
                inv.setInvoiceId(invoice.getId());
                return inv;
            }).collect(Collectors.toList());

            invoiceDTO.setItems(itemsInvoice);
            dto.setInvoice(invoiceDTO);
        }

        return dto;
    }
}
