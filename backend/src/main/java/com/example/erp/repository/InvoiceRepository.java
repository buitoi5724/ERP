package com.example.erp.repository;

import com.example.erp.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	Optional<Invoice> findByOrder_Id(Long orderId);// <== THÊM DÒNG NÀY

}