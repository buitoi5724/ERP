package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.ProductCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * =================================================================================
 * LỚP PRODUCTSERVICE - BỘ PHẬN XỬ LÝ LOGIC NGHIỆP VỤ CHO "SẢN PHẨM"
 * =================================================================================
 * Đây là "bộ não" xử lý tất cả các yêu cầu liên quan đến sản phẩm.
 * Nó điều phối công việc giữa Controller (giao diện) và Repository (database).
 */

@Service // 👨‍💼 Đánh dấu đây là một lớp Service.
public class ProductService {

    // Tiêm (inject) các repository cần thiết để làm việc với CSDL.
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;
    
    // Tự động đọc đường dẫn thư mục upload từ file `application.properties`.
    // Đây là cách làm tốt hơn nhiều so với việc ghi cứng đường dẫn trong code.
    @Value("${upload.folder}")
    private String uploadFolder;

    /**
     * 📖 Lấy tất cả sản phẩm.
     * @Transactional(readOnly = true): Tối ưu hóa cho các tác vụ chỉ đọc, giúp tăng hiệu năng.
     */
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    /**
     * 🆔 Lấy sản phẩm theo ID.
     */
    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 🖼️ Lấy dữ liệu byte của hình ảnh từ hệ thống file.
     */
    @Transactional(readOnly = true)
    public byte[] getImage(Long id) throws IOException {
        // 1. Tìm sản phẩm trong CSDL để lấy tên file ảnh.
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        // 2. Kiểm tra xem sản phẩm có thông tin ảnh không.
        if (product.getImage() == null || product.getImage().isEmpty()) {
            return null;
        }

        // 3. Ghép đường dẫn thư mục upload với tên file để có đường dẫn đầy đủ.
        Path imagePath = Paths.get(uploadFolder, product.getImage());

        // 4. Kiểm tra xem file có thực sự tồn tại và đọc file.
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }

        return null;
    }

    /**
     * ➕ Lưu một sản phẩm (dành cho cả tạo mới và cập nhật đơn giản không kèm file).
     */
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * 🔄 Cập nhật thông tin sản phẩm, bao gồm cả việc thay đổi ảnh.
     */
    @Transactional
    public Product update(Long id, Product updatedProduct, MultipartFile imageFile) throws IOException {
        // 1. Lấy sản phẩm hiện có từ CSDL. Nếu không tìm thấy sẽ báo lỗi.
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        // 2. Cập nhật các thuộc tính của sản phẩm.
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());

        // 3. Cập nhật danh mục (category).
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
            ProductCategory category = categoryRepository.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category);
        }

        // 4. Xử lý file ảnh mới nếu người dùng upload.
        if (imageFile != null && !imageFile.isEmpty()) {
            // Xóa ảnh cũ đi để tránh rác.
            if (existingProduct.getImage() != null) {
                deleteImageFile(existingProduct.getImage());
            }
            // Lưu ảnh mới và lấy về tên file.
            String newImageName = saveImageFile(imageFile);
            // Cập nhật tên ảnh mới cho sản phẩm.
            existingProduct.setImage(newImageName);
        }

        // 5. Lưu lại sản phẩm đã được cập nhật vào CSDL.
        return productRepository.save(existingProduct);
    }


    /**
     * 🗑️ Xóa một sản phẩm theo ID.
     */
    @Transactional
    public void delete(Long id) {
        // Trước khi xóa sản phẩm khỏi DB, hãy xóa file ảnh liên quan.
        productRepository.findById(id).ifPresent(product -> {
            if (product.getImage() != null) {
                deleteImageFile(product.getImage());
            }
        });
        
        productRepository.deleteById(id);
    }
    
    // =================================================================================
    // CÁC HÀM HỖ TRỢ (PRIVATE HELPERS)
    // =================================================================================

    /**
     * Hàm private để xử lý việc lưu file ảnh và trả về tên file duy nhất.
     */
    private String saveImageFile(MultipartFile imageFile) throws IOException {
        // Lấy đuôi file gốc (ví dụ: .png, .jpg).
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Tạo tên file mới bằng UUID để đảm bảo không bao giờ bị trùng.
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Tạo đường dẫn đầy đủ tới nơi sẽ lưu file.
        Path filePath = Paths.get(uploadFolder, uniqueFileName);

        // Tạo thư mục nếu nó chưa tồn tại.
        Files.createDirectories(filePath.getParent());
        
        // Chuyển file từ bộ nhớ tạm sang file thật trên ổ đĩa.
        imageFile.transferTo(filePath);
        
        // Trả về tên file duy nhất đã được lưu.
        return uniqueFileName;
    }
    
    /**
     * Hàm private để xử lý việc xóa một file ảnh khỏi ổ đĩa.
     */
    private void deleteImageFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadFolder, fileName);
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            // Ghi lại lỗi thay vì để chương trình chết.
            // Trong ứng dụng thực tế, bạn nên dùng một logger chuyên nghiệp.
            System.err.println("Lỗi khi xóa file ảnh: " + fileName + " - " + e.getMessage());
        }
    }
}