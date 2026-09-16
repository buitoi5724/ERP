package com.example.erp.repository;

import com.example.erp.entity.CMFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CMFileRepository extends JpaRepository<CMFile, Long> {

    // Lấy tất cả file của một yêu cầu
    List<CMFile> findByRequestId(Long requestId);

    // Lấy file của yêu cầu, sắp xếp mới nhất trước
    List<CMFile> findByRequestIdOrderByUploadedAtDesc(Long requestId);
}