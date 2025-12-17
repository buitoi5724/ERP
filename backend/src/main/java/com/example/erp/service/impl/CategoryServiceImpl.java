package com.example.erp.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.CategoryRequestDTO;
import com.example.erp.dto.CategoryResponseDTO;
import com.example.erp.entity.Category;
import com.example.erp.repository.CategoryRepository;
import com.example.erp.service.CategoryService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO dto) {

        if (categoryRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());

        Category saved = categoryRepository.save(category);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found"));

        return toResponse(category);
    }


@Override
@Transactional(readOnly = true)
public List<CategoryResponseDTO> getAll() {
    return categoryRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
}
    /* ===== MAPPER NHẸ ===== */
    private CategoryResponseDTO toResponse(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
