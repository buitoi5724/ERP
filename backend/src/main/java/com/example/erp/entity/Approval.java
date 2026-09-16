package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yêu cầu cần phê duyệt
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private RequestApprove request;

    // Trạng thái của quá trình phê duyệt
    // Ví dụ: PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private String status;

    // Thời điểm bắt đầu quy trình phê duyệt
    @Column(nullable = false)
    private LocalDateTime startedAt;

    // Thời điểm hoàn thành quy trình
    private LocalDateTime completedAt;

    public Approval() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestApprove getRequest() {
        return request;
    }

    public void setRequest(RequestApprove request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}