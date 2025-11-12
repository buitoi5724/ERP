// File: src/main/java/com/example/erp/service/OrderService.java
package com.example.erp.service;

import com.example.erp.dto.OrderDTO;
import com.example.erp.dto.OrderItemDTO;
import com.example.erp.entity.Order;
import com.example.erp.entity.OrderItem;
import com.example.erp.entity.Product; 
import com.example.erp.repository.OrderRepository;
import com.example.erp.repository.ProductRepository; 
import com.example.erp.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo; 

    // (Các phương thức getAll, getOrderById giữ nguyên)
    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepo.findById(id);
        return order.orElse(null);
    }

    // (Hàm createOrder đã chính xác, giữ nguyên)
    @Transactional
    public Order createOrder(Order order) {
        order.setCode(CodeGenerator.generateCode("ORD"));
        order.setOrderDate(LocalDateTime.now());
        
        double calculatedSubtotal = 0.0;

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                
                if (item.getProductId() == null) {
                    throw new IllegalArgumentException("Dữ liệu đơn hàng không hợp lệ: productId bị thiếu.");
                }

                Product product = productRepo.findById(item.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với ID: " + item.getProductId()));

                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setOrder(order); 
                calculatedSubtotal += (item.getPrice() * item.getQuantity());
            }
        }
        
        order.setSubtotal(calculatedSubtotal);
        
        Double tax = order.getTax() != null ? order.getTax() : 0.0;
        Double shipping = order.getShippingFee() != null ? order.getShippingFee() : 0.0;
        Double discount = order.getDiscount() != null ? order.getDiscount() : 0.0;

        order.setTotalAmount(calculatedSubtotal + tax + shipping - discount);

        return orderRepo.save(order);
    }


    // === PHƯƠNG THỨC CONVERTTODTO (ĐÃ SỬA LỖI) ===
    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();

        // === SỬA LỖI TẠI ĐÂY ===
        // Thêm ID của đơn hàng vào DTO để frontend có thể điều hướng
        // chính xác đến /invoice/{id}
        dto.setId(order.getId());
        // ---------------------

        // Gán thông tin cơ bản
        dto.setOrderCode(order.getCode());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");
        dto.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        dto.setCreatedBy(order.getCreatedBy());
        dto.setNote(order.getNote());

        // Gán thông tin khách hàng
        dto.setCustomerName(order.getCustomerName());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setAddress(order.getAddress());

        // Gán thông tin thanh toán (đã sửa dùng 'Double')
        dto.setSubtotal(order.getSubtotal() != null ? order.getSubtotal() : 0.0);
        dto.setTax(order.getTax() != null ? order.getTax() : 0.0);
        dto.setShippingFee(order.getShippingFee() != null ? order.getShippingFee() : 0.0);
        dto.setDiscount(order.getDiscount() != null ? order.getDiscount() : 0.0);
        dto.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : 0.0);
        dto.setPaymentMethod(order.getPaymentMethod());

        // Gán danh sách sản phẩm
        List<OrderItemDTO> itemsDTO = order.getItems()
                .stream()
                .map(this::convertItemToDTO) // Sử dụng hàm helper
                .collect(Collectors.toList());
        
        dto.setItems(itemsDTO);

        return dto;
    }

    // (Hàm convertItemToDTO giữ nguyên)
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item.getProductId());
        itemDTO.setProductName(item.getProductName());
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setPrice(item.getPrice());
        return itemDTO;
    }
}