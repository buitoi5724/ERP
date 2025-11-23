package com.example.erp.service;

import com.example.erp.entity.ShoppingCart;
import com.example.erp.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository cartRepo;
// file này chưa logic chính sử lý trong này
    // Lấy giỏ hàng theo user
    public List<ShoppingCart> getCartByUser(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    // Thêm sản phẩm vào giỏ
    public ShoppingCart addToCart(Long userId, Long productId, int quantity, long accountId) {
        Optional <ShoppingCart> existingCart = cartRepo.findByUserIdAndProductId(userId, productId);
    
        if (existingCart.isPresent()) {
            // Nếu đã có sản phẩm thì cộng dồn số lượng
            ShoppingCart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepo.save(cart);
        } else {
            // Nếu chưa có thì thêm mới
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setAccountId(accountId);
            return cartRepo.save(cart);
        }
    }

    // Cập nhật số lượng
    public ShoppingCart updateQuantity(Long cartId, int quantity) {
        ShoppingCart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setQuantity(quantity);
        return cartRepo.save(cart);
    }

    // Xóa sản phẩm khỏi giỏ
    public void removeFromCart(Long cartId) {
        cartRepo.deleteById(cartId);
    }

    // Xóa toàn bộ giỏ theo user
    public void clearCart(Long userId) {
        cartRepo.deleteAllByUserId(userId);
    }
    public void removeMultipleFromCart(List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) return;
        cartRepo.deleteAllById(cartIds);
    }
}
