package com.example.erp.service;

import com.example.erp.dto.OrderRequestDTO;
import com.example.erp.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO dto);

    OrderResponseDTO getOrderById(Long id);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO updateOrder(Long id, OrderRequestDTO dto);

    void cancelOrder(Long id);

    void confirmOrder(Long id); // chuyển sang trạng thái CONFIRMED
}
