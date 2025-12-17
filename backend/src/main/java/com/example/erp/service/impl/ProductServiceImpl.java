package com.example.erp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.erp.dto.ProductRequestDTO;
import com.example.erp.dto.ProductResponseDTO;
import com.example.erp.entity.Category;
import com.example.erp.entity.Product;
import com.example.erp.mapper.ProductMapper;
import com.example.erp.repository.CategoryRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.ProductService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    // Thư mục lưu ảnh
    private final String uploadFolder = "D:/uploads/product/";

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    /* ================= CREATE ================= */
    @Override
    public ProductResponseDTO create(ProductRequestDTO dto) {
        return create(dto, new MultipartFile[0]);
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO dto, MultipartFile[] images) {
        // Kiểm tra và xử lý trùng code
        String code = dto.getCode();
        int suffix = 1;
        while (productRepository.existsByCode(code)) {
            code = dto.getCode() + "_" + suffix++;
        }
        dto.setCode(code);

        // Lấy category
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Chuyển DTO thành entity
        Product product = productMapper.toEntity(dto, category);

        // Xử lý ảnh nếu có và lưu vào folder
        if (images != null && images.length > 0) {
            productMapper.addImages(product, images, uploadFolder);
        }

        // Lưu product
        Product saved = productRepository.save(product);

        // Chuyển entity thành DTO trả về
        ProductResponseDTO response = productMapper.toResponseDTO(saved);

        // Thông báo nếu code đã bị đổi
        if (!dto.getCode().equals(code)) {
            response.setDescription(response.getDescription() +
                    " (Note: Product code was auto-changed to '" + code + "' due to duplication)");
        }

        return response;
    }

    /* ================= UPDATE ================= */
    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        return update(id, dto, new MultipartFile[0]);
    }

    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto, MultipartFile[] images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        productMapper.updateEntity(product, dto, category);

        if (images != null && images.length > 0) {
            productMapper.addImages(product, images, uploadFolder);
        }

        Product updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }

    /* ================= GET ================= */
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
    	Product product = productRepository.findByIdWithGalleries(id)
    	        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }



    /* ================= CHECK CODE ================= */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return productRepository.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllProductCodes() {
        return productRepository.findAll()
                .stream()
                .map(Product::getCode)
                .collect(Collectors.toList());
    }
    @Override
    public ProductResponseDTO updateMainImage(Long productId, String imageUrl) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        // Cập nhật ảnh đại diện
        product.setImage(imageUrl);

        Product updated = productRepository.save(product);
        return productMapper.toResponseDTO(updated);
    }
    /* ================= DELETE ================= */
    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
        productRepository.delete(product);
    
    }
}
