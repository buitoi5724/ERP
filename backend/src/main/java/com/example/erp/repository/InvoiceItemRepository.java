package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.InvoiceItem;
import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
}
