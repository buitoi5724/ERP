package com.example.erp.controller;

import com.example.erp.entity.ShoppingCart;
import com.example.erp.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000") // Cho React gọi
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/{userId}")
    public List<ShoppingCart> getCart(@PathVariable Long userId) {
        return shoppingCartService.getCartByUser(userId);
    }

    @PostMapping("/add")
    public ShoppingCart addToCart(@RequestParam Long userId,
                                  @RequestParam Long productId,
                                  @RequestParam int quantity,
                                  @RequestParam Long accountId) {
        return shoppingCartService.addToCart(userId, productId, quantity, accountId);
    }

    @PutMapping("/update/{cartId}")
    public ShoppingCart updateQuantity(@PathVariable Long cartId,
                                       @RequestParam int quantity) {
        return shoppingCartService.updateQuantity(cartId, quantity);
    }

    @DeleteMapping("/remove/{cartId}")
    public void removeFromCart(@PathVariable Long cartId) {
        shoppingCartService.removeFromCart(cartId);
    }

    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable Long userId) {
        shoppingCartService.clearCart(userId);
    }
}
