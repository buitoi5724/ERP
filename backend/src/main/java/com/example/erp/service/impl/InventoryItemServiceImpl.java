package com.example.erp.service.impl;

import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryItemResponseDTO;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryItem;
import com.example.erp.repository.InventoryItemRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.service.InventoryItemService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryItemServiceImpl(
            InventoryItemRepository itemRepository,
            InventoryRepository inventoryRepository) {
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // =====================================================
    // NHẬP KHO – TẠO ITEM
    // =====================================================
    @Override
    public InventoryItemResponseDTO addItem(InventoryItemRequestDTO dto) {

        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        InventoryItem item = new InventoryItem();
        item.setInventoryId(inventory.getId());
        item.setSupplierId(dto.getSupplierId());
        item.setBatchNumber(dto.getBatchNumber());
        item.setSerialNumber(dto.getSerialNumber());
        item.setExpirationDate(dto.getExpirationDate());

        item.setQuantity(dto.getQuantity());
        item.setRemainingQuantity(dto.getQuantity());

        item.setReceivedDate(LocalDate.now());
        item.setStatus("AVAILABLE");

        InventoryItem saved = itemRepository.save(item);

        return toDTO(saved);
    }

    // =====================================================
    // XUẤT KHO – THEO ITEM
    // =====================================================
    @Override
    public InventoryItemResponseDTO exportItem(Long itemId, Long customerId) {

        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("InventoryItem not found"));

        if (item.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("Hết hàng");
        }

        item.setRemainingQuantity(item.getRemainingQuantity() - 1);
        item.setCustomerId(customerId);

        if (item.getRemainingQuantity() == 0) {
            item.setStatus("SOLD");
        }

        return toDTO(itemRepository.save(item));
    }

    // =====================================================
    // QUERY
    // =====================================================
    @Override
    public List<InventoryItemResponseDTO> getAllItems() {
        return itemRepository.findAll()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItemResponseDTO> getAvailableItems() {
        return itemRepository
                .findByRemainingQuantityGreaterThanAndDeletedFalse(0)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryItemResponseDTO getItemById(Long itemId) {
        return toDTO(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Item not found"))
        );
    }

    @Override
    public List<InventoryItemResponseDTO> getItemsByCustomer(Long customerId) {
        return itemRepository.findByCustomerId(customerId)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // MAPPING
    // =====================================================
    private InventoryItemResponseDTO toDTO(InventoryItem item) {
        InventoryItemResponseDTO dto = new InventoryItemResponseDTO();
        dto.setItemId(item.getId());
        dto.setInventoryId(item.getInventoryId());
        dto.setSupplierId(item.getSupplierId());
        dto.setCustomerId(item.getCustomerId());
        dto.setBatchNumber(item.getBatchNumber());
        dto.setSerialNumber(item.getSerialNumber());
        dto.setQuantity(item.getQuantity());
        dto.setRemainingQuantity(item.getRemainingQuantity());
        dto.setReceivedDate(item.getReceivedDate());
        dto.setExpirationDate(item.getExpirationDate());
        dto.setStatus(item.getStatus());
        return dto;
    }
}