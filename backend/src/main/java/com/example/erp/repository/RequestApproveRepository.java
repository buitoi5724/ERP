
package com.example.erp.repository;

import com.example.erp.entity.RequestApprove;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RequestApproveRepository
        extends JpaRepository<RequestApprove, Long> {

    Optional<RequestApprove> findByRequestCode(String requestCode);

    List<RequestApprove> findByEmployeeEmpIdOrderByCreatedAtDesc(
            Long empId
    );

    List<RequestApprove> findByStatusOrderByCreatedAtDesc(
            String status
    );

    boolean existsByRequestCode(String requestCode);

    /**
     * Tìm các đơn nghỉ bị trùng lịch
     * của nhân viên khác trong cùng phòng ban.
     *
     * Các đơn được kiểm tra:
     * - NGHI_PHEP
     * - OM
     * - NGHI_KHONG_LUONG
     * - NGHI_VIEC_RIENG
     * - NGHI_THAI_SAN
     * - NGHI_BU
     *
     * Chỉ kiểm tra đơn PENDING và APPROVED.
     */
    @Query("""
        SELECT r
        FROM RequestApprove r
        WHERE r.requestType IN (
            'NGHI_PHEP',
            'OM',
            'NGHI_KHONG_LUONG',
            'NGHI_VIEC_RIENG',
            'NGHI_THAI_SAN',
            'NGHI_BU'
        )
          AND r.status IN ('PENDING', 'APPROVED')
          AND r.employee.empId <> :employeeId
          AND r.startDate IS NOT NULL
          AND r.endDate IS NOT NULL
          AND r.startDate <= :endDate
          AND r.endDate >= :startDate
          AND EXISTS (
              SELECT ed.id
              FROM EmployeeDepartment ed
              WHERE ed.employee.empId = r.employee.empId
                AND ed.department.deptId = :departmentId
                AND ed.fromDate <= :endDate
                AND ed.toDate >= :startDate
          )
        ORDER BY r.startDate ASC
    """)
    List<RequestApprove> findOverlappingLeaveRequests(
            @Param("employeeId") Long employeeId,
            @Param("departmentId") Long departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
