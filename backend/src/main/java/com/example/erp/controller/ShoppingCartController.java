package com.example.erp.controller;

import com.example.erp.entity.ShoppingCart;
import com.example.erp.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(
	    origins = "http://localhost:3000",
	    allowedHeaders = "*",
	    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
	)
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/{userId}")
    public List<ShoppingCart> getCart(@PathVariable Long userId) {
        return shoppingCartService.getCartByUser(userId);
    }
// lấy danh sách sản phẩm trong giỏ hàng của người dùng trả về item trong giỏ hàng  

    @PostMapping("/add")
    public ShoppingCart addToCart(@RequestParam Long userId,
                                  @RequestParam Long productId,
                                  @RequestParam int quantity,
                                  @RequestParam Long accountId) {
        return shoppingCartService.addToCart(userId, productId, quantity, accountId);
    }
//lấy các giá trị của từng data có trong from 
    @PutMapping("/update/{cartId}")
    public ShoppingCart updateQuantity(@PathVariable Long cartId,
                                       @RequestParam int quantity) {
        return shoppingCartService.updateQuantity(cartId, quantity);
    }
// cập nhật thoogn tin cũng như số lượng 
    @DeleteMapping("/remove/{cartId}")
    public void removeFromCart(@PathVariable Long cartId) {
        shoppingCartService.removeFromCart(cartId);
    }
// xóa mottj san phẩm của trong một giỏ hàng 
    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable Long userId) {
        shoppingCartService.clearCart(userId);
    }
 // Xóa nhiều sản phẩm trong giỏ hàng cùng lúc
    @DeleteMapping("/remove-multiple")
    public void removeMultipleFromCart(@RequestBody List<Long> cartIds) {
        shoppingCartService.removeMultipleFromCart(cartIds); // ✅ Gọi đúng
    }
    
}