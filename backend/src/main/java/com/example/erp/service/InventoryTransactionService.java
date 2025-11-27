package com.example.erp.service;

import com.example.erp.entity.Inventory;
import com.example.erp.entity.OrderItem;
import com.example.erp.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryTransactionService {

    private final InventoryRepository inventoryRepo;

    public InventoryTransactionService(InventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    @Transactional
    public void decreaseStock(List<OrderItem> items) {
        // Lấy inventory theo productId
        Map<Long, Inventory> inventoryMap = inventoryRepo.findByProductIdIn(
                items.stream().map(OrderItem::getProductId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(inv -> inv.getProduct().getId(), inv -> inv));

        for (OrderItem item : items) {
            Inventory inventory = inventoryMap.get(item.getProductId());
            if (inventory == null) {
                throw new RuntimeException("Không tìm thấy hàng tồn kho cho sản phẩm " + item.getProductId());
            }

            int newQty = inventory.getQuantity() - item.getQuantity();
            if (newQty < 0) {
                throw new RuntimeException(
                    "Đơn Hàng Không Đủ Số Lượng Trong Kho: " 
                    + item.getProductName() 
                    + ". Chỉ còn " + inventory.getQuantity() + " sản phẩm."
                );
            }

            inventory.setQuantity(newQty);
        }

        inventoryRepo.saveAll(inventoryMap.values());
    }

    @Transactional
    public void increaseStock(List<OrderItem> items) {
        // Lấy tất cả Inventory theo productId trong order
        Map<Long, Inventory> inventoryMap = inventoryRepo.findAllById(
                items.stream()
                        .map(OrderItem::getProductId)
                        .collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(inv -> inv.getProduct().getId(), inv -> inv));

        // Cộng kho
        for (OrderItem item : items) {
            Inventory inventory = inventoryMap.get(item.getProductId());
            if (inventory == null) {
                throw new RuntimeException("Inventory not found for product " + item.getProductId());
            }

            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
        }

        inventoryRepo.saveAll(inventoryMap.values());
    }
}
