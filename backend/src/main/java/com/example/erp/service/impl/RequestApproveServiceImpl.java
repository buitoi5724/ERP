package com.example.erp.service.impl;

import com.example.erp.entity.Approval;
import com.example.erp.entity.ApprovalLine;
import com.example.erp.entity.DepartmentApprover;
import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.entity.RequestApprove;

import com.example.erp.repository.ApprovalLineRepository;
import com.example.erp.repository.ApprovalRepository;
import com.example.erp.repository.DepartmentApproverRepository;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.RequestApproveRepository;
import com.example.erp.service.CurrentUserService;

import com.example.erp.service.RequestApproveService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RequestApproveServiceImpl
        implements RequestApproveService {

    private final RequestApproveRepository requestApproveRepository;

    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    private final DepartmentApproverRepository departmentApproverRepository;

    private final ApprovalRepository approvalRepository;

    private final ApprovalLineRepository approvalLineRepository;
    
    private final CurrentUserService currentUserService;
    
    public RequestApproveServiceImpl(
            RequestApproveRepository requestApproveRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository,
            DepartmentApproverRepository departmentApproverRepository,
            ApprovalRepository approvalRepository,
            ApprovalLineRepository approvalLineRepository,
            CurrentUserService currentUserService) {

        this.requestApproveRepository = requestApproveRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.departmentApproverRepository = departmentApproverRepository;
        this.approvalRepository = approvalRepository;
        this.approvalLineRepository = approvalLineRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getAll() {

        return requestApproveRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RequestApprove getById(Long id) {

        return requestApproveRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy yêu cầu với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public RequestApprove getByRequestCode(
            String requestCode) {

        return requestApproveRepository
                .findByRequestCode(requestCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy yêu cầu với mã: "
                                        + requestCode
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getByEmployee(
            Long empId) {

        return requestApproveRepository
                .findByEmployeeEmpIdOrderByCreatedAtDesc(empId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getByStatus(
            String status) {

        return requestApproveRepository
                .findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public RequestApprove create(
            RequestApprove requestApprove) {

        // ==============================
        // 1. VALIDATE REQUEST
        // ==============================

        if (requestApprove.getRequestCode() == null
                || requestApprove.getRequestCode()
                        .trim().isEmpty()) {

            throw new RuntimeException(
                    "Mã yêu cầu không được để trống"
            );
        }

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        requestApprove.setEmployee(currentEmployee);

        if (requestApprove.getRequestType() == null
                || requestApprove.getRequestType()
                        .trim().isEmpty()) {

            throw new RuntimeException(
                    "Loại yêu cầu không được để trống"
            );
        }

        if (requestApprove.getTitle() == null
                || requestApprove.getTitle()
                        .trim().isEmpty()) {

            throw new RuntimeException(
                    "Tiêu đề không được để trống"
            );
        }

        if (requestApprove.getStatus() == null
                || requestApprove.getStatus()
                        .trim().isEmpty()) {

            requestApprove.setStatus("PENDING");
        }

        if (requestApprove.getCreatedAt() == null) {

            requestApprove.setCreatedAt(
                    LocalDateTime.now()
            );
        }

        if (requestApprove.getUpdatedAt() == null) {

            requestApprove.setUpdatedAt(
                    requestApprove.getCreatedAt()
            );
        }

        if (requestApproveRepository
                .existsByRequestCode(
                        requestApprove.getRequestCode())) {

            throw new RuntimeException(
                    "Mã yêu cầu đã tồn tại: "
                            + requestApprove.getRequestCode()
            );
        }

        // ==============================
        // 2. LƯU REQUEST
        // ==============================

        RequestApprove savedRequest =
                requestApproveRepository.save(
                        requestApprove
                );

        // ==============================
        // 3. TÌM DEPARTMENT CỦA EMPLOYEE
        // ==============================

        Employee employee =
                savedRequest.getEmployee();

        EmployeeDepartment employeeDepartment =
                employeeDepartmentRepository
                        .findByEmployeeEmpIdAndToDate(
                                employee.getEmpId(),
                                LocalDate.of(9999, 12, 31)
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Nhân viên chưa được "
                                                + "gán phòng ban"
                                )
                        );

        Long deptId =
                employeeDepartment
                        .getDepartment()
                        .getDeptId();

        // ==============================
        // 4. LẤY DANH SÁCH NGƯỜI DUYỆT
        // ==============================

        List<DepartmentApprover> approvers =
                departmentApproverRepository
                        .findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
                                deptId
                        );

        if (approvers.isEmpty()) {

            throw new RuntimeException(
                    "Phòng ban chưa được cấu hình "
                            + "người phê duyệt"
            );
        }

        // ==============================
        // 5. TẠO APPROVAL
        // ==============================

        Approval approval = new Approval();

        approval.setRequest(savedRequest);
        approval.setStatus("PENDING");
        approval.setStartedAt(
                LocalDateTime.now()
        );

        Approval savedApproval =
                approvalRepository.save(
                        approval
                );

        // ==============================
        // 6. TẠO APPROVAL LINE
        // ==============================

        for (int i = 0; i < approvers.size(); i++) {

            DepartmentApprover departmentApprover =
                    approvers.get(i);

            ApprovalLine approvalLine =
                    new ApprovalLine();

            approvalLine.setApproval(
                    savedApproval
            );

            approvalLine.setApprover(
                    departmentApprover.getEmployee()
            );

            approvalLine.setApprovalOrder(
                    departmentApprover
                            .getApprovalOrder()
            );

            // Người đầu tiên được duyệt ngay
            if (i == 0) {

                approvalLine.setStatus(
                        "PENDING"
                );

            } else {

                approvalLine.setStatus(
                        "WAITING"
                );
            }

            approvalLineRepository.save(
                    approvalLine
            );
        }

        // ==============================
        // 7. TRẢ REQUEST
        // ==============================

        return savedRequest;
    }

    @Override
    public RequestApprove update(
            Long id,
            RequestApprove requestApprove) {

        RequestApprove existing =
                getById(id);

        if (requestApprove.getRequestType() != null
                && !requestApprove.getRequestType()
                        .trim().isEmpty()) {

            existing.setRequestType(
                    requestApprove.getRequestType()
            );
        }

        if (requestApprove.getTitle() != null
                && !requestApprove.getTitle()
                        .trim().isEmpty()) {

            existing.setTitle(
                    requestApprove.getTitle()
            );
        }

        if (requestApprove.getContent() != null) {

            existing.setContent(
                    requestApprove.getContent()
            );
        }

        if (requestApprove.getStatus() != null
                && !requestApprove.getStatus()
                        .trim().isEmpty()) {

            existing.setStatus(
                    requestApprove.getStatus()
            );
        }

        existing.setUpdatedAt(
                LocalDateTime.now()
        );

        return requestApproveRepository.save(
                existing
        );
    }

    @Override
    public RequestApprove cancel(Long id) {

        RequestApprove existing =
                getById(id);

        if ("APPROVED".equals(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Không thể hủy yêu cầu đã được duyệt"
            );
        }

        if ("REJECTED".equals(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Không thể hủy yêu cầu đã bị từ chối"
            );
        }

        if ("CANCELLED".equals(
                existing.getStatus())) {

            throw new RuntimeException(
                    "Yêu cầu đã được hủy trước đó"
            );
        }

        existing.setStatus("CANCELLED");

        existing.setUpdatedAt(
                LocalDateTime.now()
        );

        return requestApproveRepository.save(
                existing
        );
    }
}