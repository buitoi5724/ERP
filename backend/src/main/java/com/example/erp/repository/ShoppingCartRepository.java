package com.example.erp.repository;

import com.example.erp.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    List<ShoppingCart> findByUserId(Long userId);

    Optional<ShoppingCart> findByUserIdAndProductId(Long userId, Long productId);

    void deleteAllByUserId(Long userId);

    // 🔹 Thêm dòng này để ProductService có thể xóa giỏ hàng chứa sản phẩm bị xóa
    void deleteAllByProductId(Long productId);

        void deleteAllByIdInBatch(Iterable<Long> ids);
    
}
