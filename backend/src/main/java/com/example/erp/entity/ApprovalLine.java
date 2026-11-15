package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_line")
public class ApprovalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quy trình phê duyệt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", nullable = false)
    private Approval approval;

    // Người được phân công phê duyệt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_emp_id", nullable = false)
    private Employee approver;

    // Thứ tự phê duyệt
    @Column(nullable = false)
    private Integer approvalOrder;

    // Trạng thái của bước này
    // WAITING, PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private String status;

    // Thời điểm thực hiện phê duyệt
    private LocalDateTime approvedAt;

    // Ý kiến của người phê duyệt
    @Column(columnDefinition = "TEXT")
    private String comment;

    // Người thực tế thực hiện thao tác
    // Có thể khác approver nếu có ủy quyền
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_emp_id")
    private Employee actionEmployee;

    public ApprovalLine() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    public Employee getApprover() {
        return approver;
    }

    public void setApprover(Employee approver) {
        this.approver = approver;
    }

    public Integer getApprovalOrder() {
        return approvalOrder;
    }

    public void setApprovalOrder(Integer approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Employee getActionEmployee() {
        return actionEmployee;
    }

    public void setActionEmployee(Employee actionEmployee) {
        this.actionEmployee = actionEmployee;
    }
}