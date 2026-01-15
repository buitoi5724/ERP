package com.example.erp.service;

import com.example.erp.dto.CustomerRequestDTO;
import com.example.erp.dto.CustomerResponseDTO;
import org.springframework.data.domain.*;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO create(CustomerRequestDTO dto);

    CustomerResponseDTO update(Long id, CustomerRequestDTO dto);

    void delete(Long id);

    CustomerResponseDTO getById(Long id);

    List<CustomerResponseDTO> getAll();

    Page<CustomerResponseDTO> search(String keyword, Pageable pageable);
}
