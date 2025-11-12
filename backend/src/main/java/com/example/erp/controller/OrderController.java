// File: src/main/java/com/example/erp/controller/OrderController.java
package com.example.erp.controller;

import com.example.erp.dto.OrderDTO;
import com.example.erp.entity.Order;
import com.example.erp.service.OrderService;
import jakarta.persistence.EntityNotFoundException; // Import thêm
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import thêm
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
// === SỬA LỖI CORS/404 TẠI ĐÂY ===
// Thêm @CrossOrigin tại ĐẦU LỚP (Class) để áp dụng cho TẤT CẢ các API bên trong
@CrossOrigin(origins = "http://localhost:3000") 
public class OrderController {

    @Autowired
    private OrderService orderService;

    // === API NÀY ĐANG BỊ 404 (Do thiếu @CrossOrigin ở trên) ===
    // API để Frontend gọi lấy hóa đơn
    // GET http://localhost:8080/api/orders/invoice/57
    @GetMapping("/invoice/{id}")
    public ResponseEntity<OrderDTO> getInvoiceById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        
        if (order == null) {
            // Lỗi này cũng có thể xảy ra nếu bạn gọi GET quá nhanh
            // trước khi transaction của POST kịp commit (lưu vào CSDL)
            return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy
        }
        
        // Chuyển đổi sang DTO trước khi trả về
        OrderDTO orderDTO = orderService.convertToDTO(order);
        return ResponseEntity.ok(orderDTO); // Trả về 200 OK với dữ liệu DTO
    }

    // === API NÀY ĐÃ CHẠY THÀNH CÔNG ===
    // API để tạo mới đơn hàng
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            Order newOrder = orderService.createOrder(order);
            OrderDTO newOrderDTO = orderService.convertToDTO(newOrder);
            return ResponseEntity.ok(newOrderDTO); // Trả về DTO (đã có 'id')

        } catch (IllegalArgumentException e) {
            // Lỗi 400 (ví dụ: productId bị thiếu)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (EntityNotFoundException e) {
            // Lỗi 404 (ví dụ: gửi productId không tồn tại)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            
        } catch (Exception e) {
            // Lỗi 500 chung
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ nội bộ: " + e.getMessage());
        }
    }

    // === API LẤY TẤT CẢ ĐƠN HÀNG (VÍ DỤ) ===
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAll();
        
        List<OrderDTO> orderDTOs = orders.stream()
            .map(orderService::convertToDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(orderDTOs);
    }
}