
package com.example.erp.service.impl;

import com.example.erp.dto.ApprovalLineResponse;
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
public class ApprovalLineServiceImpl implements ApprovalLineService {

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
            CurrentUserService currentUserService) {

        this.approvalLineRepository = approvalLineRepository;
        this.employeeRepository = employeeRepository;
        this.approvalRepository = approvalRepository;
        this.requestApproveRepository = requestApproveRepository;
        this.currentUserService = currentUserService;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getAll() {
        return approvalLineRepository.findAll();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ApprovalLine getById(Long id) {
        return approvalLineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy ApprovalLine với ID: " + id
                        )
                );
    }

    // =========================================================
    // GET BY APPROVAL ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLineResponse> getByApprovalId(Long approvalId) {

        return approvalLineRepository
                .findByApprovalIdOrderByApprovalOrderAsc(approvalId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // GET BY APPROVAL ID + ORDER
    // =========================================================

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

    // =========================================================
    // GET BY APPROVER
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getByApprover(
            Long empId,
            String status) {

        return approvalLineRepository
                .findByApproverEmpIdAndStatus(empId, status);
    }

    // =========================================================
    // GET BY ACTION EMPLOYEE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLine> getByActionEmployee(
            Long empId,
            String status) {

        return approvalLineRepository
                .findByActionEmployeeEmpIdAndStatus(empId, status);
    }

    // =========================================================
    // MY PENDING APPROVALS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLineResponse> getMyPendingApprovals() {

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        if (currentEmployee == null) {
            throw new RuntimeException(
                    "Tài khoản chưa được liên kết với Employee"
            );
        }

        List<ApprovalLine> lines =
                approvalLineRepository
                        .findByApproverEmpIdAndStatusAndApprovalRequestStatus(
                                currentEmployee.getEmpId(),
                                "PENDING",
                                "PENDING"
                        );

        return lines.stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // MY APPROVAL HISTORY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLineResponse> getMyApprovalHistory() {

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        if (currentEmployee == null) {
            throw new RuntimeException(
                    "Tài khoản chưa được liên kết với Employee"
            );
        }

        List<ApprovalLine> lines =
                approvalLineRepository
                        .findByActionEmployeeEmpIdOrderByApprovedAtDesc(
                                currentEmployee.getEmpId()
                        );

        return lines.stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public ApprovalLine create(ApprovalLine approvalLine) {

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
                || approvalLine.getStatus().isBlank()) {
            approvalLine.setStatus("WAITING");
        }

        return approvalLineRepository.save(approvalLine);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public ApprovalLine update(Long id, ApprovalLine approvalLine) {

        ApprovalLine existing = getById(id);

        if (approvalLine.getApprover() != null) {
            existing.setApprover(approvalLine.getApprover());
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
                && !approvalLine.getStatus().isBlank()) {
            existing.setStatus(approvalLine.getStatus());
        }

        if (approvalLine.getComment() != null) {
            existing.setComment(approvalLine.getComment());
        }

        return approvalLineRepository.save(existing);
    }

    // =========================================================
    // CHECK CURRENT APPROVER
    // =========================================================

    private Employee getAuthorizedEmployee(ApprovalLine line) {

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        if (currentEmployee == null) {
            throw new RuntimeException(
                    "Tài khoản chưa được liên kết với Employee"
            );
        }

        Employee approver = line.getApprover();

        if (approver == null) {
            throw new RuntimeException(
                    "ApprovalLine chưa có người duyệt"
            );
        }

        // Admin được phép xử lý theo chính sách hiện tại.
        // Người dùng thông thường chỉ được xử lý dòng giao cho mình.
        if (!currentUserService.isCurrentUserAdmin()
                && !approver.getEmpId().equals(
                        currentEmployee.getEmpId()
                )) {

            throw new RuntimeException(
                    "Bạn không có quyền xử lý đơn này"
            );
        }

        return currentEmployee;
    }

    // =========================================================
    // CHECK APPROVAL PROCESS
    // =========================================================

    private List<ApprovalLine> validateApprovalProcess(
            ApprovalLine currentLine) {

        if (!"PENDING".equals(currentLine.getStatus())) {
            throw new RuntimeException(
                    "Chỉ được xử lý bước đang PENDING"
            );
        }

        Approval approval = currentLine.getApproval();

        if (approval == null || approval.getRequest() == null) {
            throw new RuntimeException(
                    "Không tìm thấy Approval hoặc Request"
            );
        }

        RequestApprove request = approval.getRequest();

        if (!"PENDING".equals(approval.getStatus())
                || !"PENDING".equals(request.getStatus())) {
            throw new RuntimeException(
                    "Đơn không còn trong quy trình phê duyệt"
            );
        }

        List<ApprovalLine> lines =
                approvalLineRepository
                        .findByApprovalIdOrderByApprovalOrderAsc(
                                approval.getId()
                        );

        // Kiểm tra tất cả các bước trước phải APPROVED
        for (ApprovalLine line : lines) {

            if (line.getApprovalOrder()
                    < currentLine.getApprovalOrder()
                    && !"APPROVED".equals(line.getStatus())) {

                throw new RuntimeException(
                        "Bạn phải chờ hoàn tất bước "
                                + line.getApprovalOrder()
                );
            }
        }

        return lines;
    }

    // =========================================================
    // APPROVE
    // =========================================================

    @Override
    public ApprovalLineResponse approve(
            Long id,
            Long actionEmployeeId,
            String comment) {

        // 1. Lấy dòng phê duyệt
        ApprovalLine currentLine = getById(id);

        // 2. Kiểm tra người đang đăng nhập có quyền duyệt
        Employee actionEmployee =
                getAuthorizedEmployee(currentLine);

        // 3. Kiểm tra trạng thái và thứ tự phê duyệt
        List<ApprovalLine> lines =
                validateApprovalProcess(currentLine);

        Approval approval = currentLine.getApproval();
        RequestApprove request = approval.getRequest();

        // 4. Hoàn thành bước hiện tại
        currentLine.setStatus("APPROVED");
        currentLine.setActionEmployee(actionEmployee);
        currentLine.setApprovedAt(LocalDateTime.now());
        currentLine.setComment(comment);

        ApprovalLine savedLine =
                approvalLineRepository.save(currentLine);

        // 5. Tìm bước tiếp theo đang WAITING
        ApprovalLine nextLine = null;

        for (ApprovalLine line : lines) {

            if (line.getApprovalOrder()
                    > currentLine.getApprovalOrder()
                    && "WAITING".equals(line.getStatus())) {

                if (nextLine == null
                        || line.getApprovalOrder()
                        < nextLine.getApprovalOrder()) {
                    nextLine = line;
                }
            }
        }

        // 6. Nếu còn bước tiếp theo, kích hoạt người duyệt kế tiếp
        if (nextLine != null) {

            nextLine.setStatus("PENDING");

            approvalLineRepository.save(nextLine);

            // Đơn tổng thể vẫn PENDING
            request.setStatus("PENDING");
            request.setUpdatedAt(LocalDateTime.now());

            requestApproveRepository.save(request);

            return toResponse(savedLine);
        }

        // 7. Không còn bước nào phải duyệt
        boolean allApproved = lines.stream()
                .allMatch(line ->
                        line.getId().equals(currentLine.getId())
                                || "APPROVED".equals(line.getStatus())
                );

        if (!allApproved) {
            throw new RuntimeException(
                    "Quy trình chưa hoàn tất hoặc còn bước chưa được duyệt"
            );
        }

        // 8. Chỉ lúc này mới hoàn thành đơn
        approval.setStatus("APPROVED");
        approval.setCompletedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());

        requestApproveRepository.save(request);

        return toResponse(savedLine);
    }

    // =========================================================
    // REJECT
    // =========================================================

    @Override
    public ApprovalLineResponse reject(
            Long id,
            Long actionEmployeeId,
            String comment) {

        // 1. Lấy dòng phê duyệt
        ApprovalLine currentLine = getById(id);

        // 2. Kiểm tra quyền
        Employee actionEmployee =
                getAuthorizedEmployee(currentLine);

        // 3. Kiểm tra quy trình
        List<ApprovalLine> lines =
                validateApprovalProcess(currentLine);

        Approval approval = currentLine.getApproval();
        RequestApprove request = approval.getRequest();

        // 4. Ghi nhận từ chối
        currentLine.setStatus("REJECTED");
        currentLine.setActionEmployee(actionEmployee);
        currentLine.setApprovedAt(LocalDateTime.now());
        currentLine.setComment(comment);

        ApprovalLine savedLine =
                approvalLineRepository.save(currentLine);

        // 5. Hủy các bước còn lại
        for (ApprovalLine line : lines) {

            if (!line.getId().equals(currentLine.getId())
                    && ("PENDING".equals(line.getStatus())
                    || "WAITING".equals(line.getStatus()))) {

                line.setStatus("CANCELLED");
                approvalLineRepository.save(line);
            }
        }

        // 6. Kết thúc quy trình
        approval.setStatus("REJECTED");
        approval.setCompletedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        // 7. Cập nhật đơn
        request.setStatus("REJECTED");
        request.setUpdatedAt(LocalDateTime.now());

        requestApproveRepository.save(request);

        return toResponse(savedLine);
    }

    // =========================================================
    // CONVERT APPROVAL LINE -> DTO
    // =========================================================

    private ApprovalLineResponse toResponse(ApprovalLine line) {

        ApprovalLineResponse response =
                new ApprovalLineResponse();

        response.setId(line.getId());
        response.setApprovalOrder(line.getApprovalOrder());
        response.setStatus(line.getStatus());
        response.setApprovedAt(line.getApprovedAt());
        response.setComment(line.getComment());

        response.setApprover(
                toEmployeeResponse(line.getApprover())
        );

        response.setActionEmployee(
                toEmployeeResponse(line.getActionEmployee())
        );

        Approval approval = line.getApproval();

        if (approval != null) {

            ApprovalLineResponse.ApprovalResponse approvalResponse =
                    new ApprovalLineResponse.ApprovalResponse();

            approvalResponse.setId(approval.getId());
            approvalResponse.setStatus(approval.getStatus());
            approvalResponse.setStartedAt(approval.getStartedAt());
            approvalResponse.setCompletedAt(approval.getCompletedAt());

            RequestApprove request = approval.getRequest();

            if (request != null) {

                ApprovalLineResponse.RequestResponse requestResponse =
                        new ApprovalLineResponse.RequestResponse();

                requestResponse.setId(request.getId());
                requestResponse.setRequestCode(request.getRequestCode());
                requestResponse.setRequestType(request.getRequestType());
                requestResponse.setTitle(request.getTitle());
                requestResponse.setContent(request.getContent());
                requestResponse.setStatus(request.getStatus());
                requestResponse.setCreatedAt(request.getCreatedAt());
                requestResponse.setUpdatedAt(request.getUpdatedAt());

                requestResponse.setEmployee(
                        toEmployeeResponse(request.getEmployee())
                );

                approvalResponse.setRequest(requestResponse);
            }

            response.setApproval(approvalResponse);
        }

        return response;
    }

    // =========================================================
    // CONVERT EMPLOYEE -> DTO
    // =========================================================

    private ApprovalLineResponse.EmployeeResponse toEmployeeResponse(
            Employee employee) {

        if (employee == null) {
            return null;
        }

        ApprovalLineResponse.EmployeeResponse response =
                new ApprovalLineResponse.EmployeeResponse();

        response.setEmpId(employee.getEmpId());
        response.setEmpCode(employee.getEmpCode());
        response.setEmpName(employee.getEmpName());
        response.setEmail(employee.getEmail());
        response.setActive(employee.getActive());

        return response;
    }
}
