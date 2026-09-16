package com.example.erp.service;

import com.example.erp.entity.RequestTemplate;

import java.util.List;

public interface RequestTemplateService {

    // Lấy tất cả template
    List<RequestTemplate> getAll();

    // Lấy template theo ID
    RequestTemplate getById(Long id);

    // Lấy template theo mã
    RequestTemplate getByTemplateCode(
            String templateCode
    );

    // Lấy template theo loại yêu cầu
    List<RequestTemplate> getByRequestType(
            String requestType
    );

    // Lấy các template đang hoạt động
    List<RequestTemplate> getActive();

    // Tạo template
    RequestTemplate create(
            RequestTemplate requestTemplate
    );

    // Cập nhật template
    RequestTemplate update(
            Long id,
            RequestTemplate requestTemplate
    );

    // Xóa template
    void delete(Long id);
}