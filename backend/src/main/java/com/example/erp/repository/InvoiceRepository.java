package com.example.erp.repository;
import com.example.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
