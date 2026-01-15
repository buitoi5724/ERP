package com.example.erp.service;

import java.util.List;

import com.example.erp.dto.CategoryRequestDTO;
import com.example.erp.dto.CategoryResponseDTO;

public interface CategoryService {

    CategoryResponseDTO create(CategoryRequestDTO dto);

    CategoryResponseDTO getById(Long id);
    List<CategoryResponseDTO> getAll();

}
