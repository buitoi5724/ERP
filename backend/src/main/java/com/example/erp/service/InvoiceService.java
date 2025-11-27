package com.example.erp.service;

import com.example.erp.dto.InvoiceDTO;
import com.example.erp.dto.InventoryDTO;
import com.example.erp.dto.OrderDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.Order;
import com.example.erp.entity.OrderItem;
import com.example.erp.entity.Payment;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.PaymentRepository;
import com.example.erp.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    // ================== Thanh toán invoice ==================
    @Transactional
    public Payment payInvoice(Long invoiceId, String paymentMethod, Long accountId) throws Exception {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new Exception("Invoice không tồn tại"));

        // Kiểm tra đã thanh toán chưa
        boolean isPaid = invoice.getPayments() != null && !invoice.getPayments().isEmpty();
        if (isPaid) {
            throw new Exception("Invoice đã được thanh toán");
        }

        // --- TRỪ KHO --- 
        if (invoice.getOrder() != null && invoice.getOrder().getItems() != null) {
            inventoryTransactionService.decreaseStock(invoice.getOrder().getItems());
        }

        // Tạo Payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getTotalAmount());
        payment.setMethod(paymentMethod);
        payment.setAccountId(accountId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCode("PAY-" + System.currentTimeMillis());
        Payment savedPayment = paymentRepo.save(payment);

        // Cập nhật Invoice
        invoice.setPaymentMethod(paymentMethod);
        invoice.setStatus("DONE");
        invoiceRepo.save(invoice);

        // Cập nhật Order
        if (invoice.getOrder() != null) {
            invoice.getOrder().setStatus(OrderStatus.DONE);
            Long userId = invoice.getOrder().getAccountId();
            if (userId != null) {
                cartService.clearCart(userId);
            }
        }

        return savedPayment;
    }

    // ================== Hoàn trả invoice ==================
    @Transactional
    public void refundInvoice(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new Exception("Invoice không tồn tại"));

        boolean isPaid = invoice.getPayments() != null && !invoice.getPayments().isEmpty();
        if (!isPaid) {
            throw new Exception("Invoice chưa thanh toán");
        }

        // Cộng lại tồn kho
        if (invoice.getOrder() != null && invoice.getOrder().getItems() != null) {
            inventoryTransactionService.increaseStock(invoice.getOrder().getItems());
        }

        // Xóa payment (hoặc đánh dấu refund nếu muốn lưu lịch sử)
        paymentRepo.deleteAll(invoice.getPayments());

        // Cập nhật Invoice
        invoice.setStatus("REFUND");
        invoiceRepo.save(invoice);

        // Cập nhật Order
        if (invoice.getOrder() != null) {
            invoice.getOrder().setStatus(OrderStatus.PENDING);
        }
    }

    // ================== Lấy invoice theo orderId ==================
    public Invoice getByOrderId(Long orderId) {
        return invoiceRepo.findByOrder_Id(orderId).orElse(null);
    }

    // ================== Chuyển Invoice sang DTO ==================
    public InvoiceDTO convertToDTO(Invoice invoice) {
        if (invoice == null) return null;

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setPaymentStatus(
                (invoice.getPayments() != null && !invoice.getPayments().isEmpty()) ||
                "COD".equalsIgnoreCase(invoice.getPaymentMethod()) ? "PAID" : "PENDING"
        );

        if (invoice.getOrder() != null) {
            Order order = invoice.getOrder();
            dto.setOrderId(order.getId());
            dto.setOrderCode(order.getCode());
            dto.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : null);
            dto.setAmount(invoice.getTotalAmount());
            dto.setCustomerId(order.getCustomerId());
            dto.setPhone(order.getPhone());
            dto.setSubtotal(order.getSubtotal());
            dto.setTax(order.getTax());
            dto.setShippingFee(order.getShippingFee());
            dto.setDiscount(order.getDiscount());
            dto.setNote(order.getNote());

            if (order.getItems() != null) {
                List<InventoryDTO> items = order.getItems().stream().map(item -> {
                    InventoryDTO i = new InventoryDTO();
                    i.setProductId(item.getProductId());
                    i.setProductName(item.getProductName());
                    i.setPrice(item.getPrice());
                    i.setQuantity(item.getQuantity());
                    return i;
                }).collect(Collectors.toList());
                dto.setItems(items);
            }
        }

        return dto;
    }
}
