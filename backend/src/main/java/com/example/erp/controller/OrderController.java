package com.example.erp.controller;

import org.springframework.web.bind.annotation.*;
import com.example.erp.dto.OrderRequestDTO;
import com.example.erp.dto.OrderResponseDTO;
import com.example.erp.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponseDTO create(@RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<OrderResponseDTO> getAll() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public OrderResponseDTO update(@PathVariable Long id, @RequestBody OrderRequestDTO dto) {
        return orderService.updateOrder(id, dto);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @PostMapping("/{id}/confirm")
    public void confirm(@PathVariable Long id) {
        orderService.confirmOrder(id);
    }
}
