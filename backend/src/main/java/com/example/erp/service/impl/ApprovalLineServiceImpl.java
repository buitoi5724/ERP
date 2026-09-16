package com.example.erp.service.impl;

import com.example.erp.entity.Approval;
import com.example.erp.entity.ApprovalLine;
import com.example.erp.entity.Employee;
import com.example.erp.entity.RequestApprove;

import com.example.erp.repository.ApprovalLineRepository;
import com.example.erp.repository.ApprovalRepository;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.repository.RequestApproveRepository;

import com.example.erp.service.ApprovalLineService;
import com.example.erp.service.CurrentUserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApprovalLineServiceImpl
        implements ApprovalLineService {

	private final CurrentUserService currentUserService;
	
    private final ApprovalLineRepository approvalLineRepository;

    private final EmployeeRepository employeeRepository;

    private final ApprovalRepository approvalRepository;

    private final RequestApproveRepository requestApproveRepository;

    public ApprovalLineServiceImpl(
            ApprovalLineRepository approvalLineRepository,
            EmployeeRepository employeeRepository,
            ApprovalRepository approvalRepository,
            RequestApproveRepository requestApproveRepository,
            CurrentUserService currentUserService ) {

        this.approvalLineRepository = approvalLineRepository;
        this.employeeRepository = employeeRepository;
        this.approvalRepository = approvalRepository;
        this.requestApproveRepository = requestApproveRepository;
        this.currentUserService = currentUserService;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getAll() {

        return approvalLineRepository.findAll();
    }
    // =====================================================
    // GET BY ID
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public ApprovalLine getById(Long id) {

        return approvalLineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy ApprovalLine với ID: "
                                        + id
                        )
                );
    }
    // =====================================================
    // GET BY APPROVAL ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getByApprovalId(
            Long approvalId) {

        return approvalLineRepository
                .findByApprovalIdOrderByApprovalOrderAsc(
                        approvalId
                );
    }

    // =====================================================
    // GET BY APPROVAL ID + ORDER
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ApprovalLine getByApprovalIdAndOrder(
            Long approvalId,
            Integer approvalOrder) {

        return approvalLineRepository
                .findByApprovalIdAndApprovalOrder(
                        approvalId,
                        approvalOrder
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy ApprovalLine với Approval ID: "
                                        + approvalId
                                        + ", Order: "
                                        + approvalOrder
                        )
                );
    }
    // =====================================================
    // GET BY APPROVER
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getByApprover(
            Long empId,
            String status) {

        return approvalLineRepository
                .findByApproverEmpIdAndStatus(
                        empId,
                        status
                );
    }

    // =====================================================
    // GET BY ACTION EMPLOYEE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getByActionEmployee(
            Long empId,
            String status) {

        return approvalLineRepository
                .findByActionEmployeeEmpIdAndStatus(
                        empId,
                        status
                );
    }

    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public ApprovalLine create(
            ApprovalLine approvalLine) {

        if (approvalLine.getApproval() == null) {

            throw new RuntimeException(
                    "Approval không được để trống"
            );
        }

        if (approvalLine.getApprover() == null) {

            throw new RuntimeException(
                    "Người duyệt không được để trống"
            );
        }

        if (approvalLine.getApprovalOrder() == null
                || approvalLine.getApprovalOrder() <= 0) {

            throw new RuntimeException(
                    "Thứ tự duyệt phải lớn hơn 0"
            );
        }

        if (approvalLine.getStatus() == null
                || approvalLine.getStatus()
                        .trim()
                        .isEmpty()) {

            approvalLine.setStatus("WAITING");
        }

        return approvalLineRepository.save(
                approvalLine
        );
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public ApprovalLine update(
            Long id,
            ApprovalLine approvalLine) {

        ApprovalLine existing =
                getById(id);

        if (approvalLine.getApprover() != null) {

            existing.setApprover(
                    approvalLine.getApprover()
            );
        }

        if (approvalLine.getApprovalOrder() != null) {

            if (approvalLine.getApprovalOrder() <= 0) {

                throw new RuntimeException(
                        "Thứ tự duyệt phải lớn hơn 0"
                );
            }

            existing.setApprovalOrder(
                    approvalLine.getApprovalOrder()
            );
        }

        if (approvalLine.getStatus() != null
                && !approvalLine.getStatus()
                        .trim()
                        .isEmpty()) {

            existing.setStatus(
                    approvalLine.getStatus()
            );
        }

        if (approvalLine.getComment() != null) {

            existing.setComment(
                    approvalLine.getComment()
            );
        }

        return approvalLineRepository.save(
                existing
        );
    }

    // =====================================================
    // APPROVE
    // =====================================================

    @Override
    public ApprovalLine approve(
            Long id,
            Long actionEmployeeId,
            String comment) {

        // ---------------------------------------------
        // 1. Lấy ApprovalLine hiện tại
        // ---------------------------------------------

        ApprovalLine currentLine =
                getById(id);

        // ---------------------------------------------
        // 2. Kiểm tra trạng thái
        // ---------------------------------------------

        if (!"PENDING".equals(
                currentLine.getStatus())) {

            throw new RuntimeException(
                    "Chỉ có dòng phê duyệt đang PENDING mới được duyệt"
            );
        }

        // ---------------------------------------------
        // 3. Lấy nhân viên thực hiện
        // ---------------------------------------------

        Employee actionEmployee =
                employeeRepository
                        .findById(actionEmployeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy nhân viên xử lý với ID: "
                                                + actionEmployeeId
                                                
                                                
                                )
                                
                        );

        // ---------------------------------------------
        // 4. Cập nhật line hiện tại
        // ---------------------------------------------

        currentLine.setStatus(
                "APPROVED"
        );

        currentLine.setActionEmployee(
                actionEmployee
        );

        currentLine.setApprovedAt(
                LocalDateTime.now()
        );

        currentLine.setComment(
                comment
        );

        ApprovalLine savedLine =
                approvalLineRepository.save(
                        currentLine
                );

        // ---------------------------------------------
        // 5. Lấy Approval
        // ---------------------------------------------

        Approval approval =
                currentLine.getApproval();

        if (approval == null) {

            throw new RuntimeException(
                    "Approval không tồn tại"
            );
        }

        Long approvalId =
                approval.getId();

        // ---------------------------------------------
        // 6. Lấy tất cả ApprovalLine
        // ---------------------------------------------

        List<ApprovalLine> lines =
                approvalLineRepository
                        .findByApprovalIdOrderByApprovalOrderAsc(
                                approvalId
                        );

        // ---------------------------------------------
        // 7. Tìm người duyệt tiếp theo
        // ---------------------------------------------

        ApprovalLine nextLine = null;

        for (ApprovalLine line : lines) {

            if ("WAITING".equals(
                    line.getStatus())) {

                nextLine = line;

                break;
            }
        }

        // ---------------------------------------------
        // 8. Nếu còn người duyệt
        // ---------------------------------------------

        if (nextLine != null) {

            nextLine.setStatus(
                    "PENDING"
            );

            approvalLineRepository.save(
                    nextLine
            );

            return savedLine;
        }

        // ---------------------------------------------
        // 9. Không còn người duyệt
        //    → Approval hoàn thành
        // ---------------------------------------------

        approval.setStatus(
                "APPROVED"
        );

        approval.setCompletedAt(
                LocalDateTime.now()
        );

        approvalRepository.save(
                approval
        );

        // ---------------------------------------------
        // 10. Cập nhật RequestApprove
        // ---------------------------------------------

        RequestApprove request =
                approval.getRequest();

        if (request == null) {

            throw new RuntimeException(
                    "RequestApprove không tồn tại"
            );
        }

        request.setStatus(
                "APPROVED"
        );

        request.setUpdatedAt(
                LocalDateTime.now()
        );

        requestApproveRepository.save(
                request
        );

        return savedLine;
    }

    // =====================================================
    // REJECT
    // =====================================================

    @Override
    public ApprovalLine reject(
            Long id,
            Long actionEmployeeId,
            String comment) {

        // ---------------------------------------------
        // 1. Lấy ApprovalLine
        // ---------------------------------------------

        ApprovalLine existing =
                getById(id);

        // ---------------------------------------------
        // 2. Kiểm tra trạng thái
        // ---------------------------------------------

        if (!"PENDING".equals(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Chỉ có dòng phê duyệt đang PENDING mới được từ chối"
            );
        }

        // ---------------------------------------------
        // 3. Lấy nhân viên thực hiện
        // ---------------------------------------------

        Employee actionEmployee =
                employeeRepository
                        .findById(actionEmployeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy nhân viên xử lý với ID: "
                                                + actionEmployeeId
                                )
                        );

        // ---------------------------------------------
        // 4. Cập nhật ApprovalLine
        // ---------------------------------------------

        existing.setStatus(
                "REJECTED"
        );

        existing.setActionEmployee(
                actionEmployee
        );

        existing.setApprovedAt(
                LocalDateTime.now()
        );

        existing.setComment(
                comment
        );

        ApprovalLine savedLine =
                approvalLineRepository.save(
                        existing
                );

        // ---------------------------------------------
        // 5. Lấy Approval
        // ---------------------------------------------

        Approval approval =
                existing.getApproval();

        if (approval == null) {

            throw new RuntimeException(
                    "Approval không tồn tại"
            );
        }

        // ---------------------------------------------
        // 6. Approval bị từ chối
        // ---------------------------------------------

        approval.setStatus(
                "REJECTED"
        );

        approval.setCompletedAt(
                LocalDateTime.now()
        );

        approvalRepository.save(
                approval
        );

        // ---------------------------------------------
        // 7. Request bị từ chối
        // ---------------------------------------------

        RequestApprove request =
                approval.getRequest();

        if (request == null) {

            throw new RuntimeException(
                    "RequestApprove không tồn tại"
            );
        }

        request.setStatus(
                "REJECTED"
        );

        request.setUpdatedAt(
                LocalDateTime.now()
        );

        requestApproveRepository.save(
                request
        );

        return savedLine;
    }
}