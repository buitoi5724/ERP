package com.example.erp.repository;

import com.example.erp.entity.RequestTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestTemplateRepository
        extends JpaRepository<RequestTemplate, Long> {

    // Lấy các template đang hoạt động theo loại yêu cầu
    List<RequestTemplate> findByRequestTypeAndActiveTrue(
            String requestType
    );

    // Tìm template theo mã
    Optional<RequestTemplate> findByTemplateCode(
            String templateCode
    );

    // Kiểm tra mã template đã tồn tại chưa
    boolean existsByTemplateCode(
            String templateCode
    );

    // Lấy tất cả template đang hoạt động
    List<RequestTemplate> findByActiveTrue();
}