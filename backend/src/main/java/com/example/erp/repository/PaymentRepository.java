package com.example.erp.repository;

import com.example.erp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByInvoice_Id(Long invoiceId);
}
