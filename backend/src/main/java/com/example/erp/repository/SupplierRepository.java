package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.Supplier;
import java.util.Optional;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Kiểm tra xem Email có tồn tại chưa (dùng cho Validate)
    boolean existsByEmail(String email);

    // Kiểm tra số điện thoại đã tồn tại chưa (nếu cần)
    boolean existsByPhone(String phone);

    // Lấy nhà cung cấp theo Account ID
    Optional<Supplier> findByAccountId(Long accountId);

    // Search theo tên (chứa ký tự gần đúng) - hỗ trợ liệt kê, lọc danh sách
    List<Supplier> findByNameContainingIgnoreCase(String keyword);
}
