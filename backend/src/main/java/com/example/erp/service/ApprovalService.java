
package com.example.erp.service;

import com.example.erp.dto.ApprovalResponse;
import com.example.erp.entity.Approval;

import java.util.List;

public interface ApprovalService {

    // Lấy tất cả quy trình phê duyệt
    List<Approval> getAll();

    // Lấy approval theo ID
    Approval getById(Long id);

    // Lấy approval của một request
    ApprovalResponse getByRequestId(Long requestId);

    // Tạo approval
    Approval create(Approval approval);

    // Cập nhật thông tin approval
    Approval update(Long id, Approval approval);

    // Phê duyệt một bước và tự động chuyển cấp tiếp theo
    void approveLine(Long approvalLineId, Long actionEmpId, String comment);

    // Từ chối một bước và kết thúc quy trình
    void rejectLine(Long approvalLineId, Long actionEmpId, String comment);
}
