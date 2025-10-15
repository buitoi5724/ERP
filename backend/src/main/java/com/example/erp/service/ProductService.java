package com.example.erp.service;
import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.entity.ProductPrice;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.ProductCategoryRepository;
import com.example.erp.repository.ProductPriceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Autowired
    private ProductPriceRepository productPriceRepository;
    @Value("${upload.folder}")
    private String uploadFolder;
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public byte[] getImage(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));

        if (product.getImage() == null || product.getImage().isEmpty()) {
            return null;
        }
        Path imagePath = Paths.get(uploadFolder, product.getImage());
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }
        return null;
    }
    /**
     * ➕ Lưu sản phẩm mới và ghi giá khởi tạo vào bảng ProductPrice
     */
    @Transactional
    public Product save(Product product) {
        Product savedProduct = productRepository.save(product);
        // 🔹 Khi tạo sản phẩm mới thì ghi giá vào bảng product_price
        if (product.getPrice() != null) {
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(savedProduct);
            productPrice.setPrice(product.getPrice());
            productPrice.setStartDate(LocalDateTime.now());
            productPriceRepository.save(productPrice);
        }
        return savedProduct;
    }
    /**
     * 🔄 Cập nhật sản phẩm và tự động ghi lịch sử giá
     */
    @Transactional
    public Product update(Long id, Product updatedProduct, MultipartFile imageFile) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        // 🔹 Kiểm tra thay đổi giá
        if (updatedProduct.getPrice() != null &&
                !updatedProduct.getPrice().equals(existingProduct.getPrice())) {
            // Đóng giá cũ nếu còn hiệu lực
            ProductPrice currentPrice = productPriceRepository
                    .findFirstByProduct_IdAndEndDateIsNull(existingProduct.getId());
            if (currentPrice != null) {
                currentPrice.setEndDate(LocalDateTime.now());
                productPriceRepository.save(currentPrice);
            }
            // Thêm giá mới
            ProductPrice newPrice = new ProductPrice();
            newPrice.setProduct(existingProduct);
            newPrice.setPrice(updatedProduct.getPrice());
            newPrice.setStartDate(LocalDateTime.now());
            productPriceRepository.save(newPrice);

            // Cập nhật giá hiện tại trong bảng product
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        // Cập nhật danh mục
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
            ProductCategory category = categoryRepository.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }
        // Cập nhật ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingProduct.getImage() != null) {
                deleteImageFile(existingProduct.getImage());
            }
            String newImageName = saveImageFile(imageFile);
            existingProduct.setImage(newImageName);
        }
        return productRepository.save(existingProduct);
    }
    @Transactional
    public void delete(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            if (product.getImage() != null) {
                deleteImageFile(product.getImage());
            }
        });
        productRepository.deleteById(id);
    }
    private String saveImageFile(MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(uploadFolder, uniqueFileName);
        Files.createDirectories(filePath.getParent());
        imageFile.transferTo(filePath);
        return uniqueFileName;
    }
    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadFolder, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa file ảnh: " + fileName + " - " + e.getMessage());
        }
    }
}
