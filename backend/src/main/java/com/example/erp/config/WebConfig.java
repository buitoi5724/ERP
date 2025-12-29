package com.example.erp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ Cấu hình CORS chuẩn, fix lỗi allowCredentials + multi-port dev
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Liệt kê rõ các origin frontend/backend mà bạn dùng
                .allowedOriginPatterns(
                        "http://localhost:3000", // React dev
                        "http://localhost:8080"  // Swagger / backend
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // ✅ cho phép gửi cookie / auth header
    }

    // ✅ Serve static files (ảnh, uploads, ...)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/uploads/product/") // nhớ thêm file: và /
                .setCachePeriod(3600);
    }
}
