package com.example.erp.service.impl;

import com.example.erp.entity.Approval;
import com.example.erp.repository.ApprovalRepository;
import com.example.erp.service.ApprovalService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;

    public ApprovalServiceImpl(
            ApprovalRepository approvalRepository) {

        this.approvalRepository = approvalRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Approval> getAll() {
        return approvalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Approval getById(Long id) {

        return approvalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy Approval với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Approval getByRequestId(Long requestId) {

        return approvalRepository.findByRequestId(requestId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy Approval của Request ID: "
                                        + requestId
                        )
                );
    }

    @Override
    public Approval create(Approval approval) {

        if (approval.getRequest() == null) {
            throw new RuntimeException(
                    "Request không được để trống"
            );
        }

        if (approvalRepository.existsByRequestId(
                approval.getRequest().getId())) {

            throw new RuntimeException(
                    "Request này đã có Approval"
            );
        }

        if (approval.getStatus() == null ||
                approval.getStatus().trim().isEmpty()) {

            approval.setStatus("PENDING");
        }

        if (approval.getStartedAt() == null) {
            approval.setStartedAt(
                    LocalDateTime.now()
            );
        }

        return approvalRepository.save(approval);
    }

    @Override
    public Approval update(
            Long id,
            Approval approval) {

        Approval existing = getById(id);

        if (approval.getStatus() != null &&
                !approval.getStatus().trim().isEmpty()) {

            existing.setStatus(
                    approval.getStatus()
            );
        }

        if (approval.getStartedAt() != null) {
            existing.setStartedAt(
                    approval.getStartedAt()
            );
        }

        if (approval.getCompletedAt() != null) {
            existing.setCompletedAt(
                    approval.getCompletedAt()
            );
        }

        return approvalRepository.save(existing);
    }
}