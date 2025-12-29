package com.example.erp.service;

import java.util.List;

import com.example.erp.dto.SupplierListDTO;
import com.example.erp.dto.SupplierRequestDTO;
import com.example.erp.dto.SupplierResponseDTO;

public interface SupplierService {

    SupplierResponseDTO create(SupplierRequestDTO dto);

    SupplierResponseDTO update(Long id, SupplierRequestDTO dto);

    void delete(Long id);

    SupplierResponseDTO getById(Long id);

    List<SupplierResponseDTO> getAll();
    
    
}
