package com.example.erp.repository;

import com.example.erp.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Đây là một Repository Interface.
 * Vai trò: Cung cấp một bộ các phương thức để thực hiện các thao tác
 * CRUD (Create, Read, Update, Delete) trên đối tượng ProductCategory.
 * Nó trừu tượng hóa (che giấu) hoàn toàn việc viết các câu lệnh SQL phức tạp.
 */
@Repository // Đánh dấu interface này là một 'Repository', một bean (thành phần) được quản lý bởi Spring.
            // Nó chịu trách nhiệm cho việc truy cập và thao tác với dữ liệu.
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    // --- Thật đáng kinh ngạc, chỉ cần như vậy là đủ! ---

    // Bằng việc kế thừa JpaRepository, interface này sẽ có ngay lập tức các phương thức như:
    // - save(ProductCategory entity): Lưu hoặc cập nhật một danh mục.
    // - findById(Long id): Tìm một danh mục theo khóa chính.
    // - findAll(): Lấy tất cả các danh mục.
    // - deleteById(Long id): Xóa một danh mục theo khóa chính.
    // - count(): Đếm tổng số danh mục.
    // - và rất nhiều phương thức khác...
}