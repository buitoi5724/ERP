package com.example.erp.controller;

import com.example.erp.entity.Order;
import com.example.erp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")  // Thay đổi từ "/api/orders" thành "/api/invoices" để tránh trùng lặp
@CrossOrigin
public class InvoiceController {
    @Autowired 
    private OrderService service;

    @GetMapping  // Endpoint bây giờ là GET /api/invoices (lấy tất cả invoices)
    public List<Order> all() { 
        return service.getAll(); 
    }

    // Nếu bạn có thêm method khác (như create, update), hãy thêm vào đây và dùng mapping tương ứng, ví dụ:
    // @PostMapping("/invoice")  // POST /api/invoices/invoice
    // public Order create(@RequestBody Order order) { return service.create(order); }
}
