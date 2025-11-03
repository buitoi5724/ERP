package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.entity.ProductGallery;
import com.example.erp.entity.ProductPrice;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.ProductCategoryRepository;
import com.example.erp.repository.ProductPriceRepository;
import com.example.erp.repository.ShoppingCartRepository;
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
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Value("${upload.folder}")
    private String uploadFolder;

    // ===================== GET METHODS =====================

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);

        productOpt.ifPresent(product -> {
            if (product.getGalleries() != null) product.getGalleries().size();
            if (product.getCategory() != null) product.getCategory().getName();
        });

        return productOpt;
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với id: " + id));

        if (product.getImage() == null || product.getImage().isEmpty()) return null;

        Path imagePath = Paths.get(uploadFolder, product.getImage());
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        }
        return null;
    }

    // ===================== CREATE =====================

    @Transactional
    public Product save(Product product) throws IOException {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            ProductCategory category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);

        if (product.getPrice() != null) {
            ProductPrice productPrice = new ProductPrice();
            productPrice.setProduct(savedProduct);
            productPrice.setPrice(product.getPrice());
            productPrice.setStartDate(LocalDateTime.now());
            productPriceRepository.save(productPrice);
        }

        return savedProduct;
    }

    // ===================== UPDATE (NEW) =====================

    @Transactional
    public Product updateProductKeepExisting(
            Long id,
            Product updatedProduct,
            List<MultipartFile> imageFiles,
            String existingImagesJson
    ) throws IOException {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // ----- Cập nhật thông tin cơ bản -----
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setSizes(updatedProduct.getSizes());
        existingProduct.setColors(updatedProduct.getColors());

        // ----- Cập nhật danh mục -----
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getId() != null) {
            ProductCategory category = categoryRepository.findById(updatedProduct.getCategory().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            existingProduct.setCategory(category);
        }

        // ----- Parse danh sách ảnh cần giữ -----
        Set<String> keepImages = new HashSet<>();
        if (existingImagesJson != null && !existingImagesJson.isEmpty()) {
            existingImagesJson = existingImagesJson.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .trim();
            if (!existingImagesJson.isEmpty()) {
                keepImages.addAll(Arrays.asList(existingImagesJson.split(",")));
            }
        }

        // ----- Giữ lại ảnh cũ -----
        List<ProductGallery> galleriesToKeep = new ArrayList<>();
        if (existingProduct.getGalleries() != null) {
            for (ProductGallery g : existingProduct.getGalleries()) {
                if (keepImages.contains(g.getImageUrl())) {
                    galleriesToKeep.add(g);
                } else {
                    deleteImageFile(g.getImageUrl());
                }
            }
        }

        existingProduct.getGalleries().clear();
        existingProduct.getGalleries().addAll(galleriesToKeep);

        // ----- Thêm ảnh mới -----
        if (imageFiles != null && !imageFiles.isEmpty()) {
            Files.createDirectories(Paths.get(uploadFolder));

            for (MultipartFile file : imageFiles) {
                String fileName = saveImageFile(file);
                ProductGallery newGallery = new ProductGallery();
                newGallery.setImageUrl(fileName);
                newGallery.setProduct(existingProduct);
                existingProduct.getGalleries().add(newGallery);
            }
        }

        // ----- Cập nhật ảnh đại diện -----
        if (!existingProduct.getGalleries().isEmpty()) {
            existingProduct.setImage(existingProduct.getGalleries().get(0).getImageUrl());
        } else {
            existingProduct.setImage(null);
        }

        return productRepository.save(existingProduct);
    }

    // ===================== DELETE =====================

    @Transactional
    public void delete(Long id) {
        shoppingCartRepository.deleteAllByProductId(id);

        productRepository.findById(id).ifPresent(product -> {
            if (product.getGalleries() != null) {
                for (ProductGallery gallery : product.getGalleries()) {
                    deleteImageFile(gallery.getImageUrl());
                }
            }
        });

        productRepository.deleteById(id);
    }

    // ===================== XÓA ẢNH CỤ THỂ =====================

    @Transactional
    public void deleteGalleryImage(Long productId, String filename) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm ID: " + productId));

        if (product.getGalleries() == null || product.getGalleries().isEmpty()) {
            throw new EntityNotFoundException("Sản phẩm không có ảnh nào để xóa");
        }

        ProductGallery target = product.getGalleries().stream()
                .filter(g -> filename.equals(g.getImageUrl()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ảnh với tên: " + filename));

        deleteImageFile(target.getImageUrl());
        product.getGalleries().remove(target);

        if (filename.equals(product.getImage())) {
            if (!product.getGalleries().isEmpty()) {
                product.setImage(product.getGalleries().get(0).getImageUrl());
            } else {
                product.setImage(null);
            }
        }

        productRepository.save(product);
    }

    // ===================== SET MAIN IMAGE =====================

    @Transactional
    public void setMainImage(Long productId, Long galleryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm ID: " + productId));

        ProductGallery selected = product.getGalleries().stream()
                .filter(g -> g.getId().equals(galleryId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ảnh gallery ID: " + galleryId));

        product.setImage(selected.getImageUrl());
        productRepository.save(product);
    }

    // ===================== HELPER =====================

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
            if (fileName == null || fileName.isEmpty()) return;
            Path filePath = Paths.get(uploadFolder, fileName);
            if (Files.exists(filePath)) Files.delete(filePath);
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi xóa file ảnh: " + fileName + " - " + e.getMessage());
        }
    }
}
