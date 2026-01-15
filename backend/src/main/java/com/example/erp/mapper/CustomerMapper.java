package com.example.erp.mapper;

import com.example.erp.dto.CustomerResponseDTO;
import com.example.erp.entity.Customer;

public class CustomerMapper {

    public static CustomerResponseDTO toDTO(Customer c) {
        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(c.getId());
        dto.setAccountId(c.getAccountId());
        dto.setName(c.getName());
        dto.setPhone(c.getPhone());
        dto.setAddress(c.getAddress());
        dto.setStatus(c.getStatus());

        dto.setGroupId(c.getGroupId());
        dto.setProvinceId(c.getProvinceId());
        dto.setDistrictId(c.getDistrictId());
        dto.setWardId(c.getWardId());

        if (c.getAccount() != null) {
            dto.setUsername(c.getAccount().getUsername());
            dto.setEmail(c.getAccount().getEmail());
        }

        return dto;
    }
}
