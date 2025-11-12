package com.example.erp.service;

import com.example.erp.entity.Inventory;
import com.example.erp.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired private InventoryRepository inventoryRepo;

    public Inventory updateStock(String product, int quantityChange) {
        Optional<Inventory> opt = inventoryRepo.findAll()
                .stream().filter(i -> i.getProductName().equals(product)).findFirst();
        Inventory inv = opt.orElseGet(() -> { 
            Inventory i = new Inventory(); i.setProductName(product); i.setQuantity(0); return i; 
        });
        inv.setQuantity(inv.getQuantity() + quantityChange);
        return inventoryRepo.save(inv);
    }
}
