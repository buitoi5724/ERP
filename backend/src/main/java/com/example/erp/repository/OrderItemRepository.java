package com.example.erp.repository;
import com.example.erp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}