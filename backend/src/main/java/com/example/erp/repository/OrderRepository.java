package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCode(String code);
}
