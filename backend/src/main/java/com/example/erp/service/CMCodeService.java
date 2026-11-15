package com.example.erp.service;

import com.example.erp.entity.CMCode;

import java.util.List;

public interface CMCodeService {

    // Lấy tất cả mã cấu hình
    List<CMCode> getAll();

    // Lấy theo ID
    CMCode getById(Long id);

    // Lấy các mã đang hoạt động theo nhóm
    List<CMCode> getByCodeGroup(String codeGroup);

    // Lấy một mã theo nhóm + code
    CMCode getByCodeGroupAndCode(
            String codeGroup,
            String code
    );

    // Tạo mã cấu hình
    CMCode create(CMCode cmCode);

    // Cập nhật mã cấu hình
    CMCode update(
            Long id,
            CMCode cmCode
    );

    // Xóa mã cấu hình
    void delete(Long id);
}