package com.example.erp.service;

import com.example.erp.entity.ApprovalLine;

import java.util.List;

public interface ApprovalLineService {

    // Lấy tất cả dòng phê duyệt
    List<ApprovalLine> getAll();

    // Lấy theo ID
    ApprovalLine getById(Long id);

    // Lấy các dòng của một Approval
    List<ApprovalLine> getByApprovalId(Long approvalId);

    // Lấy một dòng theo Approval + thứ tự duyệt
    ApprovalLine getByApprovalIdAndOrder(
            Long approvalId,
            Integer approvalOrder
    );

    // Lấy các yêu cầu đang chờ một nhân viên duyệt
    List<ApprovalLine> getByApprover(
            Long empId,
            String status
    );

    // Lấy các dòng mà nhân viên đã xử lý
    List<ApprovalLine> getByActionEmployee(
            Long empId,
            String status
    );

    // Tạo dòng phê duyệt
    ApprovalLine create(ApprovalLine approvalLine);

    // Cập nhật dòng phê duyệt
    ApprovalLine update(
            Long id,
            ApprovalLine approvalLine
    );

    // Duyệt
    ApprovalLine approve(
            Long id,
            Long actionEmployeeId,
            String comment
    );

    // Từ chối
    ApprovalLine reject(
            Long id,
            Long actionEmployeeId,
            String comment
    );
}