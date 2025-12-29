package com.example.erp.mapper;

import com.example.erp.dto.SupplierListDTO;
import com.example.erp.dto.SupplierRequestDTO;
import com.example.erp.dto.SupplierResponseDTO;
import com.example.erp.entity.Supplier;

public class SupplierMapper {

	
    public static Supplier toEntityCreate(SupplierRequestDTO dto) {
        Supplier supplier = new Supplier();
        updateFields(dto, supplier);
        return supplier;
    }

    
    
    public static void toEntityUpdate(SupplierRequestDTO dto, Supplier supplier) {
        updateFields(dto, supplier);
    }

    
    
    private static void updateFields(SupplierRequestDTO dto, Supplier supplier) {
        if (dto.getName() != null) supplier.setName(dto.getName());
        if (dto.getPhone() != null) supplier.setPhone(dto.getPhone());
        if (dto.getEmail() != null) supplier.setEmail(dto.getEmail());
        if (dto.getAddress() != null) supplier.setAddress(dto.getAddress());
        if (dto.getTaxCode() != null) supplier.setTaxCode(dto.getTaxCode());
        if (dto.getActive() != null) supplier.setActive(dto.getActive());
        if (dto.getAccountId() != null) supplier.setAccountId(dto.getAccountId());
    }

    
    
    public static SupplierResponseDTO toDTO(Supplier supplier) {
        SupplierResponseDTO dto = new SupplierResponseDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        dto.setTaxCode(supplier.getTaxCode());
        dto.setActive(supplier.getActive());
        dto.setAccountId(supplier.getAccountId());
        return dto;
    }

    
    public static SupplierListDTO toListDTO(Supplier supplier) {
        SupplierListDTO dto = new SupplierListDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setActive(supplier.getActive());
        return dto;
    }
    public static SupplierListDTO toListDTO(Supplier supplier, String username) {
        SupplierListDTO dto = toListDTO(supplier);
        dto.setUsername(username);
        return dto;
    }
}
