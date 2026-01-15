package com.example.erp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.InventoryLogResponseDTO;
import com.example.erp.entity.InventoryLog;
import com.example.erp.repository.InventoryLogRepository;
import com.example.erp.service.InventoryLogService;

@Service
@Transactional(readOnly = true)
public class InventoryLogServiceImpl implements InventoryLogService {

    private final InventoryLogRepository logRepository;

    public InventoryLogServiceImpl(InventoryLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<InventoryLogResponseDTO> getByProductId(Long productId) {
        return logRepository.findByProductIdOrderByActionTimeDesc(productId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private InventoryLogResponseDTO toDTO(InventoryLog log) {
        InventoryLogResponseDTO dto = new InventoryLogResponseDTO();
        dto.setId(log.getId());
        dto.setProductId(log.getProductId());
        dto.setQuantityChange(log.getQuantityChange());
        dto.setType(log.getType());
        dto.setWarehouse(log.getWarehouse());
        dto.setActionTime(log.getActionTime());
        return dto;
    }
}
