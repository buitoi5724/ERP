package com.example.erp.service.impl;

import com.example.erp.entity.ShoppingCart;
import com.example.erp.repository.ShoppingCartRepository;
import com.example.erp.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepo;

    public ShoppingCartServiceImpl(ShoppingCartRepository cartRepo) {
        this.cartRepo = cartRepo;
    }

    // ================= GET CART =================
    @Override
    @Transactional(readOnly = true)
    public List<ShoppingCart> getCartByUser(Long userId) {
        return cartRepo.findByUserId(userId);
    }

    // ================= ADD TO CART =================
    @Override
    public ShoppingCart addToCart(Long userId, Long productId, int quantity, long accountId) {

        Optional<ShoppingCart> existingCart =
                cartRepo.findByUserIdAndProductId(userId, productId);

        if (existingCart.isPresent()) {
            ShoppingCart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepo.save(cart);
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cart.setAccountId(accountId);

        return cartRepo.save(cart);
    }

    // ================= UPDATE QUANTITY =================
    @Override
    public ShoppingCart updateQuantity(Long cartId, int quantity) {

        ShoppingCart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setQuantity(quantity);
        return cartRepo.save(cart);
    }

    // ================= REMOVE ONE ITEM =================
    @Override
    public void removeFromCart(Long cartId) {
        cartRepo.deleteById(cartId);
    }

    // ================= CLEAR CART BY USER =================
    @Override
    public void clearCart(Long userId) {
        cartRepo.deleteAllByUserId(userId);
    }

    // ================= REMOVE MULTIPLE =================
    @Override
    public void removeMultipleFromCart(List<Long> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) return;
        cartRepo.deleteAllById(cartIds);
    }

    // ================= REMOVE BY PRODUCT =================
    @Override
    public void deleteAllByProductId(long productId) {
        cartRepo.deleteAllByProductId(productId);
    }
}
