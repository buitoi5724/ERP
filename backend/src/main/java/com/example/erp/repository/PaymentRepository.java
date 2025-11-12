package com.example.erp.repository;

import com.example.erp.entity.Payment;  // Đảm bảo import entity đúng
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Thêm method tùy chỉnh nếu cần, ví dụ:
    // List<Payment> findByOrderId(Long orderId);
}