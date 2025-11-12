package com.example.erp.controller;

import com.example.erp.entity.Order;
import com.example.erp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")  // Thay đổi từ "/api/orders" thành "/api/inventory" để tránh trùng lặp
@CrossOrigin
public class InventoryController {

    @Autowired 
    private OrderService service;

    @GetMapping  // Endpoint bây giờ là GET /api/inventory (lấy tất cả inventory)
    public List<Order> all() { 
        return service.getAll(); 
    }

    // Nếu bạn có thêm method khác (như create, update), hãy thêm vào đây và dùng mapping tương ứng, ví dụ:
    // @PostMapping("/transaction")  // POST /api/inventory/transaction
    // public Order create(@RequestBody Order order) { return service.create(order); }
}
