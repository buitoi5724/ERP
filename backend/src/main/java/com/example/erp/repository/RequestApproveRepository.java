package com.example.erp.repository;

import com.example.erp.entity.RequestApprove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestApproveRepository
        extends JpaRepository<RequestApprove, Long> {

    // Tìm yêu cầu theo mã
    Optional<RequestApprove> findByRequestCode(String requestCode);

    // Lấy toàn bộ yêu cầu của một nhân viên
    List<RequestApprove> findByEmployeeEmpIdOrderByCreatedAtDesc(
            Long empId
    );

    // Lấy yêu cầu theo trạng thái
    List<RequestApprove> findByStatusOrderByCreatedAtDesc(
            String status
    );

    // Kiểm tra mã yêu cầu đã tồn tại chưa
    boolean existsByRequestCode(String requestCode);
}