package com.example.erp.service;

import com.example.erp.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    List<ShoppingCart> getCartByUser(Long userId);

    ShoppingCart addToCart(Long userId, Long productId, int quantity, long accountId);

    ShoppingCart updateQuantity(Long cartId, int quantity);

    void removeFromCart(Long cartId);

    void clearCart(Long userId);

    void removeMultipleFromCart(List<Long> cartIds);

    void deleteAllByProductId(long productId);
}
