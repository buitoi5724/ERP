
package com.example.erp.repository;

import com.example.erp.entity.ApprovalLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalLineRepository
        extends JpaRepository<ApprovalLine, Long> {

    // Lấy các bước duyệt của một Approval theo thứ tự
    List<ApprovalLine> findByApprovalIdOrderByApprovalOrderAsc(
            Long approvalId
    );

    // Tìm bước duyệt theo Approval và thứ tự
    Optional<ApprovalLine> findByApprovalIdAndApprovalOrder(
            Long approvalId,
            Integer approvalOrder
    );

    // Lấy các bước đang chờ của người duyệt
    List<ApprovalLine> findByApproverEmpIdAndStatus(
            Long empId,
            String status
    );

    // Lấy các bước đã được nhân viên thực hiện
    List<ApprovalLine> findByActionEmployeeEmpIdAndStatus(
            Long empId,
            String status
    );

    // Lấy lịch sử xử lý của nhân viên
    List<ApprovalLine>
    findByActionEmployeeEmpIdOrderByApprovedAtDesc(
            Long empId
    );

    // Lấy toàn bộ bước theo trạng thái
    List<ApprovalLine> findByStatus(String status);

    // Tìm bước tiếp theo có thứ tự lớn hơn và đang WAITING
    Optional<ApprovalLine>
    findFirstByApprovalIdAndApprovalOrderGreaterThanAndStatusOrderByApprovalOrderAsc(
            Long approvalId,
            Integer approvalOrder,
            String status
    );

    // Lấy các bước đang chờ của người duyệt,
    // đồng thời đơn tổng thể phải đang PENDING
    List<ApprovalLine>
    findByApproverEmpIdAndStatusAndApprovalRequestStatus(
            Long empId,
            String approvalLineStatus,
            String requestStatus
    );
}
