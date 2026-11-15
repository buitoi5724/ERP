package com.example.erp.repository;

import com.example.erp.entity.ApprovalDelegation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ApprovalDelegationRepository
        extends JpaRepository<ApprovalDelegation, Long> {

    // Lấy các ủy quyền đang hoạt động của người ủy quyền
    List<ApprovalDelegation> findByFromEmployeeEmpIdAndActiveTrue(
            Long empId
    );

    // Tìm ủy quyền có hiệu lực trong khoảng thời gian
    List<ApprovalDelegation> findByFromEmployeeEmpIdAndActiveTrueAndFromDateLessThanEqualAndToDateGreaterThanEqual(
            Long empId,
            LocalDate fromDate,
            LocalDate toDate
    );

    // Lấy danh sách người được ủy quyền
    List<ApprovalDelegation> findByToEmployeeEmpIdAndActiveTrue(
            Long empId
    );
}