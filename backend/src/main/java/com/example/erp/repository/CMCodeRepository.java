package com.example.erp.repository;

import com.example.erp.entity.CMCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CMCodeRepository extends JpaRepository<CMCode, Long> {

    // Lấy các mã theo nhóm
    List<CMCode> findByCodeGroupAndActiveTrueOrderByDisplayOrderAsc(
            String codeGroup
    );

    // Tìm một mã cụ thể trong nhóm
    Optional<CMCode> findByCodeGroupAndCode(
            String codeGroup,
            String code
    );

    // Kiểm tra mã đã tồn tại trong nhóm chưa
    boolean existsByCodeGroupAndCode(
            String codeGroup,
            String code
    );
}