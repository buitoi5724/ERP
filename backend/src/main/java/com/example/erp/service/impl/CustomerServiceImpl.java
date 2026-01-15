package com.example.erp.service.impl;

import com.example.erp.dto.CustomerRequestDTO;
import com.example.erp.dto.CustomerResponseDTO;
import com.example.erp.entity.Account;
import com.example.erp.entity.Customer;
import com.example.erp.mapper.CustomerMapper;
import com.example.erp.repository.AccountRepository;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.service.CustomerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (customerRepository.existsByAccountIdAndDeletedFalse(dto.getAccountId()))
            throw new RuntimeException("Account already linked");

        if (customerRepository.existsByPhoneAndDeletedFalse(dto.getPhone()))
            throw new RuntimeException("Phone already exists");

        Customer c = new Customer();
        c.setAccountId(dto.getAccountId());
        c.setName(dto.getName());
        c.setPhone(dto.getPhone());
        c.setEmail(account.getEmail()); 
        c.setAddress(dto.getAddress());
        c.setStatus(dto.getStatus());
        c.setGroupId(dto.getGroupId());
        c.setProvinceId(dto.getProvinceId());
        c.setDistrictId(dto.getDistrictId());
        c.setWardId(dto.getWardId());

        Customer saved = customerRepository.save(c);
        saved.setAccount(account); // ⭐ set account để mapper lấy
        return CustomerMapper.toDTO(saved);
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer c = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (dto.getName() != null) c.setName(dto.getName());
        if (dto.getPhone() != null) c.setPhone(dto.getPhone());
        if (dto.getAddress() != null) c.setAddress(dto.getAddress());
        if (dto.getStatus() != null) c.setStatus(dto.getStatus());

        Customer saved = customerRepository.save(c);
        return CustomerMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Customer c = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        c.setDeleted(true);
        customerRepository.save(c);
    }

    @Override
    public CustomerResponseDTO getById(Long id) {
        Customer c = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return CustomerMapper.toDTO(c);
    }

    @Override
    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAllWithAccount()
                .stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }

    @Override
    public Page<CustomerResponseDTO> search(String keyword, Pageable pageable) {
        return customerRepository.searchWithAccount(keyword, pageable)
                .map(CustomerMapper::toDTO);
    }
}
