package com.example.erp.repository;

import com.example.erp.entity.DepartmentApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentApproverRepository
        extends JpaRepository<DepartmentApprover, Long> {

    // Lấy danh sách người duyệt của một phòng ban
    List<DepartmentApprover> findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
            Long deptId
    );

    // === THÊM MỚI: Tìm tất cả người phê duyệt hoạt động của phòng ===
    List<DepartmentApprover> findByDepartmentDeptIdAndActiveTrue(Long deptId);
    
    // Lấy cấu hình người duyệt của một nhân viên
    List<DepartmentApprover> findByEmployeeEmpIdAndActiveTrue(
            Long empId
    );

    // Kiểm tra nhân viên có phải người duyệt
    // của một phòng ban hay không
    Optional<DepartmentApprover>
    findByDepartmentDeptIdAndEmployeeEmpIdAndActiveTrue(
            Long deptId,
            Long empId
    );
}