package com.example.erp.repository;

import com.example.erp.entity.DepartmentApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentApproverRepository
        extends JpaRepository<DepartmentApprover, Long> {

    // Lấy danh sách người duyệt của một phòng ban
    List<DepartmentApprover> findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
            Long deptId
    );

    // Lấy cấu hình người duyệt của một nhân viên
    List<DepartmentApprover> findByEmployeeEmpIdAndActiveTrue(
            Long empId
    );
}