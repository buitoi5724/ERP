package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_department")
public class EmployeeDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhân viên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    // Phòng ban
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    // Ngày bắt đầu vào phòng ban
    @Column(nullable = false)
    private LocalDate fromDate;

    // Ngày kết thúc
    // 9999-12-31 = vẫn đang thuộc phòng ban
    @Column(nullable = false)
    private LocalDate toDate;

    // Constructor
    public EmployeeDepartment() {
    }

    // Getter / Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
}
