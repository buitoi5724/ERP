package com.example.erp.controller;

import com.example.erp.dto.OrderDTO;
import com.example.erp.entity.Order;
import com.example.erp.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // === API tạo mới đơn hàng ===
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            Order newOrder = orderService.createOrder(order);
            OrderDTO newOrderDTO = orderService.convertToDTO(newOrder);
            return ResponseEntity.ok(newOrderDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi máy chủ nội bộ: " + e.getMessage());
        }
    }

    // === API lấy đơn hàng theo ID ===
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        OrderDTO orderDTO = orderService.convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    // === API lấy tất cả đơn hàng ===
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAll();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(orderService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }
}