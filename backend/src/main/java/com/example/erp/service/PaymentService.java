package com.example.erp.service;

import com.example.erp.entity.Invoice;
import com.example.erp.entity.InvoiceItem;
import com.example.erp.entity.Payment;
import com.example.erp.entity.Product;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.PaymentRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {


@Autowired
private PaymentRepository paymentRepo;

@Autowired
private InvoiceRepository invoiceRepo;

@Autowired
private ProductRepository productRepo;

@Autowired
private ShoppingCartRepository cartRepo;

@Transactional
public Payment payInvoice(Long invoiceId, String method, Long accountId) {
    // Lấy invoice
    Invoice invoice = invoiceRepo.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice không tồn tại với ID: " + invoiceId));

    if (invoice.isPaid()) {
        throw new RuntimeException("Invoice đã được thanh toán");
    }

    // Trừ stock sản phẩm
    for (InvoiceItem item : invoice.getItems()) {
        Product product = item.getProduct();
        int newStock = product.getStock() - item.getQuantity();
        if (newStock < 0) {
            throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng");
        }
        product.setStock(newStock);
        productRepo.save(product);
    }

    // Xóa các item đã thanh toán khỏi giỏ hàng
    Long userId = invoice.getUser().getId();
    List<Long> paidProductIds = invoice.getItems().stream()
            .map(ii -> ii.getProduct().getId())
            .collect(Collectors.toList());
    cartRepo.deleteAllByUserIdAndProductIdIn(userId, paidProductIds);

    // Tạo payment
    Payment payment = new Payment();
    payment.setInvoice(invoice);
    payment.setMethod(method);
    payment.setAmount(invoice.getTotalAmount());
    payment.setPaymentDate(LocalDateTime.now());
    payment.setPaymentCode("PAY-" + System.currentTimeMillis());

    Payment savedPayment = paymentRepo.save(payment);

    // Đánh dấu invoice đã thanh toán
    invoice.setPaid(true);
    invoiceRepo.save(invoice);

    return savedPayment;
}


}
