package com.example.erp.controller;

import com.example.erp.entity.ProductPrice;
import com.example.erp.service.ProductPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductPriceController {

    @Autowired
    private ProductPriceService productPriceService;

    // Lấy lịch sử giá theo productId
    @GetMapping("/{id}/price-history")
    public List<ProductPrice> getPriceHistory(@PathVariable Long id) {
        return productPriceService.getByProductId(id);
    }

    // Thêm giá mới cho sản phẩm
    @PostMapping("/{id}/price-history")
    public ProductPrice addPrice(@PathVariable Long id, @RequestParam Double price) {
        return productPriceService.addNewPrice(id, price);
    }
}
