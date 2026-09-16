package com.example.erp.service;

import com.example.erp.entity.Approval;

import java.util.List;

public interface ApprovalService {

    // Lấy tất cả quy trình phê duyệt
    List<Approval> getAll();

    // Lấy approval theo ID
    Approval getById(Long id);

    // Lấy approval của một request
    Approval getByRequestId(Long requestId);

    // Tạo approval
    Approval create(Approval approval);

    // Cập nhật approval
    Approval update(Long id, Approval approval);
}