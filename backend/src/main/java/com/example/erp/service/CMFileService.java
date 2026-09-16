package com.example.erp.service;

import com.example.erp.entity.CMFile;

import java.util.List;

public interface CMFileService {

    // Lấy tất cả file
    List<CMFile> getAll();

    // Lấy file theo ID
    CMFile getById(Long id);

    // Lấy các file của một yêu cầu
    List<CMFile> getByRequestId(Long requestId);

    // Lấy các file của yêu cầu, mới nhất trước
    List<CMFile> getByRequestIdOrderByUploadedAtDesc(
            Long requestId
    );

    // Thêm file
    CMFile create(CMFile cmFile);

    // Cập nhật thông tin file
    CMFile update(Long id, CMFile cmFile);

    // Xóa file
    void delete(Long id);
}