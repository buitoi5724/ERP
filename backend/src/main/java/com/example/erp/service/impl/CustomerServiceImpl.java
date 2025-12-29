package com.example.erp.service.impl;

import com.example.erp.dto.CustomerRequestDTO;
import com.example.erp.dto.CustomerResponseDTO;
import com.example.erp.entity.Customer;
import com.example.erp.repository.CustomerRepository;
import com.example.erp.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        Customer saved = repository.save(customer);

        CustomerResponseDTO response = new CustomerResponseDTO();
        BeanUtils.copyProperties(saved, response);
        return response;
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        BeanUtils.copyProperties(dto, customer);
        Customer updated = repository.save(customer);

        CustomerResponseDTO response = new CustomerResponseDTO();
        BeanUtils.copyProperties(updated, response);
        return response;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public CustomerResponseDTO getById(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerResponseDTO response = new CustomerResponseDTO();
        BeanUtils.copyProperties(customer, response);
        return response;
    }

    @Override
    public List<CustomerResponseDTO> getAll() {
        return repository.findAll().stream().map(customer -> {
            CustomerResponseDTO response = new CustomerResponseDTO();
            BeanUtils.copyProperties(customer, response);
            return response;
        }).collect(Collectors.toList());
    }
}
