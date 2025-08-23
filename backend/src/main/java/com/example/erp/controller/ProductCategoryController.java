// package khai báo không gian tên cho lớp này, giúp tổ chức code.
package com.example.erp.controller;

// Import các lớp cần thiết từ thư viện Spring Framework và Java.
import com.example.erp.entity.ProductCategory;
import com.example.erp.service.ProductCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Controller chịu trách nhiệm xử lý các yêu cầu HTTP liên quan đến Danh mục sản phẩm.
 */

// @RestController: Đánh dấu lớp này là một Controller, nơi xử lý các yêu cầu API.
// Kết quả trả về từ các phương thức trong lớp này sẽ được tự động chuyển thành JSON.
@RestController

// @CrossOrigin: Cho phép các yêu cầu từ một nguồn khác (cụ thể là ứng dụng React chạy ở http://localhost:3000)
// có thể gọi đến các API trong Controller này. Đây là một cơ chế bảo mật của trình duyệt.
@CrossOrigin(origins = "http://localhost:3000")

// @RequestMapping: Thiết lập đường dẫn gốc cho tất cả các API trong Controller này.
// Mọi endpoint sẽ bắt đầu bằng "/api/product-categories".
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    // Đây là một biến final để giữ tham chiếu đến tầng Service.
    // Tầng Service chứa logic nghiệp vụ (ví dụ: cách lấy, lưu, xóa dữ liệu).
    private final ProductCategoryService productCategoryService;

    // Đây là Constructor của lớp. Spring sẽ sử dụng nó để "tiêm" (inject) một đối tượng
    // của ProductCategoryService vào Controller. Đây gọi là Dependency Injection.
    // Nguồn dữ liệu/logic: đến từ lớp ProductCategoryService.
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    /**
     * API để lấy TẤT CẢ các danh mục sản phẩm.
     * - HTTP Method: GET
     * - URL: /api/product-categories
     */
    @GetMapping
    public List<ProductCategoryDTO> getAll() {
        // 1. LẤY DỮ LIỆU TỪ ĐÂU: Gọi đến service để lấy TẤT CẢ các đối tượng ProductCategory từ cơ sở dữ liệu.
        return productCategoryService.getAll()
                // 2. TÁC DỤNG: Xử lý dữ liệu vừa lấy được.
                .stream() // Chuyển danh sách (List) thành một luồng (Stream) để xử lý.
                // Ánh xạ (map) mỗi đối tượng ProductCategory (đầy đủ) thành một đối tượng ProductCategoryDTO .
                .map(category -> new ProductCategoryDTO(category.getId(), category.getName()))
                // Thu thập (collect) tất cả các đối tượng DTO đã tạo thành một danh sách mới.
                .collect(Collectors.toList());
    }

    /**
     * API để lấy một danh mục sản phẩm theo ID.
     * - HTTP Method: GET
     * - URL: /api/product-categories/{id} (ví dụ: /api/product-categories/1)
     */
    @GetMapping("/{id}")
    // @PathVariable Long id: LẤY DỮ LIỆU TỪ ĐÂU: Lấy giá trị 'id' từ đường dẫn URL.
    public ResponseEntity<ProductCategoryDTO> getById(@PathVariable Long id) {
        // 1. LẤY DỮ LIỆU TỪ ĐÂU: Gọi service để tìm một danh mục theo 'id' trong CSDL.
        return productCategoryService.getById(id)
                // 2. TÁC DỤNG: Xử lý kết quả.
                // Nếu tìm thấy (Optional chứa giá trị), ánh xạ nó thành DTO và trả về status 200 OK.
                .map(category -> ResponseEntity.ok(new ProductCategoryDTO(category.getId(), category.getName())))
                // Nếu không tìm thấy (Optional rỗng), trả về status 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * API để tạo một danh mục sản phẩm mới.
     * - HTTP Method: POST
     * - URL: /api/product-categories
     */
    @PostMapping
    // @RequestBody ProductCategory category: LẤY DỮ LIỆU TỪ ĐÂU: Lấy đối tượng ProductCategory từ
    // phần thân (body) của yêu cầu HTTP (thường là dữ liệu JSON do client gửi lên).
    public ProductCategory create(@RequestBody ProductCategory category) {
        // TÁC DỤNG: Gọi service để lưu đối tượng 'category' mới vào cơ sở dữ liệu.
        // Service sẽ trả về đối tượng đã được lưu (thường có thêm ID do CSDL tự tạo).
        return productCategoryService.save(category);
    }

    /**
     * API để cập nhật một danh mục sản phẩm đã có.
     * - HTTP Method: PUT
     * - URL: /api/product-categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable Long id, @RequestBody ProductCategory category) {
        // 1. LẤY DỮ LIỆU TỪ ĐÂU:
        //    - `@PathVariable Long id`: Lấy ID từ URL.
        //    - `@RequestBody ProductCategory category`: Lấy thông tin cập nhật từ body của request.
        //    - `productCategoryService.getById(id)`: Lấy đối tượng gốc từ CSDL để kiểm tra sự tồn tại.

        // 2. TÁC DỤNG:
        return productCategoryService.getById(id)
                .map(existingCategory -> { // Nếu tìm thấy danh mục với id này...
                    category.setId(id); // Đảm bảo rằng ID của đối tượng gửi lên khớp với ID trong URL.
                    // Gọi service để lưu (cập nhật) đối tượng và trả về status 200 OK.
                    return ResponseEntity.ok(productCategoryService.save(category));
                })
                .orElse(ResponseEntity.notFound().build()); // Nếu không tìm thấy, trả về 404 Not Found.
    }

    /**
     * API để xóa một danh mục sản phẩm.
     * - HTTP Method: DELETE
     * - URL: /api/product-categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // 1. LẤY DỮ LIỆU TỪ ĐÂU: Lấy ID từ URL.
        // 2. TÁC DỤNG:
        // Kiểm tra xem danh mục có tồn tại trong CSDL không.
        if (productCategoryService.getById(id).isPresent()) {
            productCategoryService.delete(id); // Nếu có, gọi service để xóa.
            // Trả về status 204 No Content - báo hiệu xóa thành công và không có nội dung gì trả về.
            return ResponseEntity.noContent().build();
        }
        // Nếu không tồn tại, trả về status 404 Not Found.
        return ResponseEntity.notFound().build();
    }

    /**
     * Đây là một lớp nội bộ (inner class) DTO (Data Transfer Object).
     * TÁC DỤNG: Định nghĩa một cấu trúc dữ liệu đơn giản chỉ chứa các thông tin cần thiết
     * để gửi cho phía client (frontend). Việc này giúp:
     * 1. Giảm lượng dữ liệu truyền đi (chỉ gửi id và name).
     * 2. Không để lộ cấu trúc đầy đủ của Entity trong database ra bên ngoài.
     */
    public static class ProductCategoryDTO {
        private Long id;
        private String name;

        // Constructor để tạo đối tượng DTO từ id và name.
        public ProductCategoryDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Các phương thức getter và setter tiêu chuẩn.
        public Long getId() { return id; }
        public String getName() { return name; }
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
    }
}