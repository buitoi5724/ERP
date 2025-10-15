package com.example.erp.controller;

import com.example.erp.entity.ProductPrice;
import com.example.erp.service.ProductPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-prices") // 🔥 đổi prefix để tránh trùng ProductController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductPriceController {

    @Autowired
    private ProductPriceService productPriceService;

    // Lấy lịch sử giá theo productId
    // GET /api/product-prices/{productId}
    @GetMapping("/{productId}")
    public List<ProductPrice> getPriceHistory(@PathVariable Long productId) {
        return productPriceService.getByProductId(productId);
    }

    // Thêm giá mới cho sản phẩm
    // POST /api/product-prices/{productId}?price=100000
    @PostMapping("/{productId}")
    public ProductPrice addPrice(@PathVariable Long productId, @RequestParam Double price) {
        return productPriceService.addNewPrice(productId, price);
    }
}
