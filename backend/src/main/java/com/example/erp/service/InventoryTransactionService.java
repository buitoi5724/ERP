package com.example.erp.service;

import com.example.erp.entity.InventoryTransaction;
import com.example.erp.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class InventoryTransactionService {
    @Autowired private InventoryTransactionRepository repo;

    public InventoryTransaction record(String product, int qty, String action) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setProductName(product);
        tx.setQuantity(qty);
        tx.setAction(Enum.valueOf(com.example.erp.util.InventoryAction.class, action));
        tx.setTransactionDate(LocalDateTime.now());
        return repo.save(tx);
    }
}
