package com.example.erp.service;

import java.util.List;
import com.example.erp.dto.InventoryLogResponseDTO;

public interface InventoryLogService {
    List<InventoryLogResponseDTO> getByProductId(Long productId);
}
