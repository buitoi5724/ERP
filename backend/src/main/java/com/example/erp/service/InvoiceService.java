package com.example.erp.service;

import com.example.erp.dto.InvoiceDTO;
import com.example.erp.dto.OrderDTO;
import com.example.erp.dto.InventoryDTO;
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
private ShoppingCartService cartService; // <-- Thêm dòng này

// ================== Thanh toán theo invoiceId + accountId ==================
@Transactional
public void payInvoice(Long invoiceId, String paymentMethod, Long accountId) throws Exception {
Invoice invoice = invoiceRepo.findById(invoiceId)
.orElseThrow(() -> new Exception("Invoice không tồn tại"));

// Kiểm tra đã thanh toán chưa
boolean isPaid = invoice.getPayments() != null && !invoice.getPayments().isEmpty();
if (isPaid) {
    throw new Exception("Invoice đã được thanh toán");
}

// Tạo Payment mới
Payment payment = new Payment();
payment.setInvoice(invoice);
payment.setAmount(invoice.getTotalAmount());
payment.setMethod(paymentMethod);
payment.setAccountId(accountId);
payment.setPaymentDate(LocalDateTime.now());
payment.setCode("PAY-" + System.currentTimeMillis());

paymentRepo.save(payment);

// Cập nhật invoice
invoice.setPaymentMethod(paymentMethod);
invoice.setStatus("DONE");
invoiceRepo.save(invoice);

// Cập nhật order nếu có
if (invoice.getOrder() != null) {
    invoice.getOrder().setStatus(OrderStatus.DONE);

    // === Xóa giỏ hàng liên quan ===
    Long userId = invoice.getOrder().getAccountId(); // Hoặc getCustomerId() nếu giỏ hàng theo customer
    if (userId != null) {
        System.out.println("Xóa giỏ hàng userId: " + userId); // debug
        cartService.clearCart(userId); // Phương thức này có @Transactional
    }
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

// ================== CRUD cơ bản ==================
public List<Invoice> getAll() {
    return invoiceRepo.findAll();
}

public Invoice getInvoiceById(Long id) {
    return invoiceRepo.findById(id).orElse(null);
}

// ================== Tạo Invoice từ OrderDTO ==================
@Transactional
public Invoice createInvoiceFromOrder(OrderDTO orderDTO) {
    Order order = mapOrderDTOToEntity(orderDTO);
    Invoice invoice = new Invoice();
    invoice.setOrder(order);
    invoice.setPaymentMethod(orderDTO.getPaymentMethod());
    invoice.setStatus("PENDING");
    invoice.setTotalAmount(orderDTO.getTotalAmount());
    return invoiceRepo.save(invoice);
}

// ================== Helper: map OrderDTO -> Order entity ==================
private Order mapOrderDTOToEntity(OrderDTO dto) {
    Order order = new Order();
    order.setCode(dto.getOrderCode());

    // convert String -> LocalDateTime
    if (dto.getOrderDate() != null) {
        order.setOrderDate(LocalDateTime.parse(dto.getOrderDate()));
    }

    // convert String -> Enum
    if (dto.getStatus() != null) {
        order.setStatus(OrderStatus.valueOf(dto.getStatus()));
    }

    order.setCustomerId(dto.getCustomerId()); // hoặc dto.getCustomerName() nếu bạn muốn lưu tên thì thêm field
    order.setAccountId(dto.getAccountId());
    order.setPhone(dto.getPhone());
    order.setSubtotal(dto.getSubtotal());
    order.setTax(dto.getTax());
    order.setShippingFee(dto.getShippingFee());
    order.setDiscount(dto.getDiscount());
    order.setPaymentMethod(dto.getPaymentMethod());
    order.setNote(dto.getNote());

    if (dto.getItems() != null) {
        List<OrderItem> items = dto.getItems().stream().map(itemDTO -> {
            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(itemDTO.getProductName());
            item.setPrice(itemDTO.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
        order.setItems(items);
    }

    return order;
}
@Transactional
public Invoice createInvoice(Invoice invoice) {
    return invoiceRepo.save(invoice);
}
}