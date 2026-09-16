package com.example.erp.repository;

import com.example.erp.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    // Tìm quy trình phê duyệt của một yêu cầu
    Optional<Approval> findByRequestId(Long requestId);

    // Kiểm tra yêu cầu đã có quy trình phê duyệt chưa
    boolean existsByRequestId(Long requestId);
}