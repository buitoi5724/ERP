
package com.example.erp.controller;

import com.example.erp.entity.Order;
import com.example.erp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments") // ✅ endpoint riêng
@CrossOrigin
public class PaymentController {

    @Autowired
    private OrderService service;

    @PostMapping
    public Order create(@RequestBody Order o) {
        return service.createOrder(o);
    }

    @GetMapping
    public List<Order> all() {
        return service.getAll();
    }
}