package com.example.erp.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.SupplierListDTO;
import com.example.erp.dto.SupplierRequestDTO;
import com.example.erp.dto.SupplierResponseDTO;
import com.example.erp.entity.Supplier;
import com.example.erp.exception.ResourceNotFoundException;
import com.example.erp.mapper.SupplierMapper;
import com.example.erp.repository.SupplierRepository;
import com.example.erp.repository.AccountRepository;
import com.example.erp.service.SupplierService;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final AccountRepository accountRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository,
                               AccountRepository accountRepository) {
        this.supplierRepository = supplierRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public SupplierResponseDTO create(SupplierRequestDTO dto) {
        if (dto.getEmail() != null && supplierRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống!");
        }

        Supplier supplier = SupplierMapper.toEntityCreate(dto);
        Supplier saved = supplierRepository.save(supplier);
        return SupplierMapper.toDTO(saved);
    }

    @Override
    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier không tồn tại!"));

        if (dto.getEmail() != null && !dto.getEmail().equals(supplier.getEmail())
                && supplierRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi nhà cung cấp khác!");
        }

        SupplierMapper.toEntityUpdate(dto, supplier);
        Supplier updated = supplierRepository.save(supplier);
        return SupplierMapper.toDTO(updated);
    }

    @Override
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier không tồn tại!"));
        supplierRepository.delete(supplier);
    }

    @Override
    public SupplierResponseDTO getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier không tồn tại!"));
        return SupplierMapper.toDTO(supplier);
    }

    @Override
    public List<SupplierResponseDTO> getAll() {
        return supplierRepository.findAll()
                .stream()
                .map(supplier -> {
                    String username = supplier.getAccountId() != null
                            ? accountRepository.findById(supplier.getAccountId())
                                    .map(a -> a.getUsername())
                                    .orElse(null)
                            : null;
                    return new SupplierResponseDTO(supplier, username);
                })
                .collect(Collectors.toList());
    }
}
