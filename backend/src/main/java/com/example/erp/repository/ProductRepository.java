package com.example.erp.repository;

import com.example.erp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * =================================================================================
 * INTERFACE PRODUCTREPOSITORY - NGƯỜI PHỤ TRÁCH TRUY CẬP DỮ LIỆU "PRODUCT"
 * =================================================================================
 * Tưởng tượng đây là một "thủ thư" chuyên quản lý kho sách "Product".
 * Nhiệm vụ của nó là cung cấp các phương thức để tìm kiếm, lưu, xóa, cập nhật
 * các cuốn sách (dòng dữ liệu) trong kho (bảng product).
 *
 * Bằng việc kế thừa JpaRepository, Spring Data JPA sẽ tự động tạo ra các chức năng
 * cơ bản cho chúng ta mà không cần viết thêm code.
 */

// JpaRepository là một interface có sẵn của Spring, cung cấp rất nhiều phương thức
// thao tác với database.
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Việc "extends JpaRepository<Product, Long>" có nghĩa là:
    // 1. Product: "Thủ thư" này sẽ quản lý các đối tượng thuộc loại "Product".
    // 2. Long: Khóa chính (ID) của "Product" có kiểu dữ liệu là "Long".

    // ==> Chỉ bằng một dòng khai báo này, chúng ta tự động có các phương thức mạnh mẽ như:
    //
    // - save(Product product): Lưu một sản phẩm mới hoặc cập nhật sản phẩm đã có.
    //
    // - findById(Long id): Tìm một sản phẩm theo ID.
    //
    // - findAll(): Lấy tất cả sản phẩm trong bảng.
    //
    // - deleteById(Long id): Xóa một sản phẩm theo ID.
    //
    // - count(): Đếm tổng số sản phẩm.
    //
    // ... và nhiều phương thức khác!

    // Bạn cũng có thể tự định nghĩa các phương thức truy vấn tùy chỉnh ở đây
    // ví dụ: List<Product> findByNameContaining(String keyword);
    // để tìm các sản phẩm có tên chứa một từ khóa nào đó.
}