package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.Invoice;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByCode(String code);
    
    
}
