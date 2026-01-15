package com.example.erp.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.erp.dto.ProductRequestDTO;
import com.example.erp.dto.ProductResponseDTO;

public interface ProductService {

    // Tạo sản phẩm bình thường
    ProductResponseDTO create(ProductRequestDTO dto);

    // Tạo sản phẩm kèm ảnh, tự xử lý code trùng
    ProductResponseDTO create(ProductRequestDTO dto, MultipartFile[] images);

    // Cập nhật sản phẩm bình thường
    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    // Cập nhật sản phẩm kèm ảnh
    ProductResponseDTO update(Long id, ProductRequestDTO dto, MultipartFile[] images);

    // Lấy sản phẩm theo ID
    ProductResponseDTO getById(Long id);

    // Lấy danh sách tất cả sản phẩm
    List<ProductResponseDTO> getAllProducts();

    // Kiểm tra code đã tồn tại hay chưa
    boolean existsByCode(String code);

    // Lấy danh sách tất cả code hiện có
    List<String> getAllProductCodes();
    
    // Cập nhật ảnh đại diện
    ProductResponseDTO updateMainImage(Long productId, String imageUrl);
    
    
    void delete(Long id);
}
