package com.example.erp.repository;

import com.example.erp.entity.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByIdAndDeletedFalse(Long id);

    boolean existsByPhoneAndDeletedFalse(String phone);
    boolean existsByAccountIdAndDeletedFalse(Long accountId);

    // ===== Lấy tất cả customers + account để FE hiển thị =====
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.account WHERE c.deleted = false")
    List<Customer> findAllWithAccount();

    // ===== SEARCH + PAGINATION với join account =====
    @Query(value = """
        SELECT c FROM Customer c
        LEFT JOIN FETCH c.account a
        WHERE c.deleted = false
          AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR c.phone LIKE CONCAT('%', :keyword, '%'))
        """,
        countQuery = "SELECT COUNT(c) FROM Customer c WHERE c.deleted = false AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR c.phone LIKE CONCAT('%', :keyword, '%'))"
    )
    Page<Customer> searchWithAccount(@Param("keyword") String keyword, Pageable pageable);
}
