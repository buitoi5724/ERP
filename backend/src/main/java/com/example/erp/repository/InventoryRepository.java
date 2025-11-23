package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	
	

}