package com.example.erp.service;

import com.example.erp.dto.ApprovalLineResponse;
import com.example.erp.entity.ApprovalLine;

import java.util.List;

public interface ApprovalLineService {

    // =========================================================
    // LẤY TẤT CẢ APPROVAL LINE
    // =========================================================

    List<ApprovalLine> getAll();


    // =========================================================
    // LẤY APPROVAL LINE THEO ID
    // =========================================================

    ApprovalLine getById(Long id);


    // =========================================================
    // LẤY APPROVAL LINE THEO APPROVAL
    // =========================================================

    List<ApprovalLineResponse> getByApprovalId(
            Long approvalId
    );


    // =========================================================
    // LẤY THEO APPROVAL + THỨ TỰ
    // =========================================================

    ApprovalLine getByApprovalIdAndOrder(
            Long approvalId,
            Integer approvalOrder
    );


    // =========================================================
    // LẤY THEO NGƯỜI PHÊ DUYỆT
    // =========================================================

    List<ApprovalLine> getByApprover(
            Long empId,
            String status
    );


    // =========================================================
    // LẤY THEO NGƯỜI THỰC HIỆN
    // =========================================================

    List<ApprovalLine> getByActionEmployee(
            Long empId,
            String status
    );


    // =========================================================
    // LẤY PHÊ DUYỆT ĐANG CHỜ CỦA TÔI
    // =========================================================

    List<ApprovalLineResponse> getMyPendingApprovals();


    // =========================================================
    // LỊCH SỬ PHÊ DUYỆT CỦA TÔI
    // =========================================================

    List<ApprovalLineResponse> getMyApprovalHistory();


    // =========================================================
    // TẠO
    // =========================================================

    ApprovalLine create(
            ApprovalLine approvalLine
    );


    // =========================================================
    // CẬP NHẬT
    // =========================================================

    ApprovalLine update(
            Long id,
            ApprovalLine approvalLine
    );


    // =========================================================
    // APPROVE
    // =========================================================

    ApprovalLineResponse approve(
            Long id,
            Long actionEmployeeId,
            String comment
    );


    // =========================================================
    // REJECT
    // =========================================================

    ApprovalLineResponse reject(
            Long id,
            Long actionEmployeeId,
            String comment
    );
}