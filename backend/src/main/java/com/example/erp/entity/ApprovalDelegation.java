package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "approval_delegation")
public class ApprovalDelegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người ủy quyền
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_emp_id", nullable = false)
    private Employee fromEmployee;

    // Người được ủy quyền
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_emp_id", nullable = false)
    private Employee toEmployee;

    // Ngày bắt đầu ủy quyền
    @Column(nullable = false)
    private LocalDate fromDate;

    // Ngày kết thúc ủy quyền
    @Column(nullable = false)
    private LocalDate toDate;

    // Trạng thái
    @Column(nullable = false)
    private Boolean active = true;

    // Lý do ủy quyền
    private String reason;

    public ApprovalDelegation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getFromEmployee() {
        return fromEmployee;
    }

    public void setFromEmployee(Employee fromEmployee) {
        this.fromEmployee = fromEmployee;
    }

    public Employee getToEmployee() {
        return toEmployee;
    }

    public void setToEmployee(Employee toEmployee) {
        this.toEmployee = toEmployee;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}