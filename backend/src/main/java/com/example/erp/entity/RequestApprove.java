package com.example.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "request_approve")
public class RequestApprove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
    
    // Mã yêu cầu
    // Ví dụ: REQ-2026-0001
    @Column(nullable = false, unique = true)
    private String requestCode;

    // Người tạo yêu cầu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    // Loại yêu cầu
    // Ví dụ: NGHI_PHEP, MUA_HANG, THANH_TOAN...
    @Column(name = "request_type", nullable = false)
    private String requestType;

    // Tiêu đề yêu cầu
    @Column(nullable = false)
    private String title;

    // Nội dung yêu cầu
    @Column(columnDefinition = "TEXT")
    private String content;

    // Trạng thái yêu cầu
    // Ví dụ: PENDING, APPROVED, REJECTED, CANCELLED
    @Column(nullable = false)
    private String status;

    // Thời điểm tạo
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Thời điểm cập nhật
    private LocalDateTime updatedAt;

    public RequestApprove() {
    }

    // =========================
    // GETTER / SETTER
    // =========================

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}