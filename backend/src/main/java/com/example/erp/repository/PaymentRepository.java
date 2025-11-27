package com.example.erp.repository;

import com.example.erp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Lấy tất cả payment theo invoiceId
    List<Payment> findByInvoiceId(Long invoiceId);
}