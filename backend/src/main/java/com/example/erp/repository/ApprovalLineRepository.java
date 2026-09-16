package com.example.erp.repository;

import com.example.erp.entity.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalLineRepository
        extends JpaRepository<ApprovalLine, Long> {

    // Lấy các bước duyệt của một approval
    List<ApprovalLine> findByApprovalIdOrderByApprovalOrderAsc(
            Long approvalId
    );

    // Tìm một bước duyệt theo thứ tự
    Optional<ApprovalLine> findByApprovalIdAndApprovalOrder(
            Long approvalId,
            Integer approvalOrder
    );

    // Lấy các yêu cầu đang chờ người duyệt xử lý
    List<ApprovalLine> findByApproverEmpIdAndStatus(
            Long empId,
            String status
    );

    // Lấy các yêu cầu mà nhân viên này đã thực hiện
    List<ApprovalLine> findByActionEmployeeEmpIdAndStatus(
            Long empId,
            String status
    );
}