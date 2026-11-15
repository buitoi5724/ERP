
package com.example.erp.service.impl;

import com.example.erp.dto.ApprovalResponse;
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
import com.example.erp.service.ApprovalService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final RequestApproveRepository requestApproveRepository;
    private final EmployeeDepartmentRepository employeeDepartmentRepository;
    private final DepartmentApproverRepository departmentApproverRepository;
    private final ApprovalLineRepository approvalLineRepository;

    public ApprovalServiceImpl(
            ApprovalRepository approvalRepository,
            RequestApproveRepository requestApproveRepository,
            EmployeeDepartmentRepository employeeDepartmentRepository,
            DepartmentApproverRepository departmentApproverRepository,
            ApprovalLineRepository approvalLineRepository) {

        this.approvalRepository = approvalRepository;
        this.requestApproveRepository = requestApproveRepository;
        this.employeeDepartmentRepository = employeeDepartmentRepository;
        this.departmentApproverRepository = departmentApproverRepository;
        this.approvalLineRepository = approvalLineRepository;
    }

    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<Approval> getAll() {
        return approvalRepository.findAll();
    }

    // =====================================================
    // GET BY ID
    // =====================================================

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

    // =====================================================
    // GET BY REQUEST ID
    // Không tự tạo Approval khi chỉ xem dữ liệu
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ApprovalResponse getByRequestId(Long requestId) {

        Approval approval = approvalRepository.findByRequestId(requestId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Yêu cầu chưa có quy trình phê duyệt"
                        )
                );

        return toResponse(approval);
    }

    // =====================================================
    // CREATE APPROVAL VÀ CÁC BƯỚC PHÊ DUYỆT
    // =====================================================

    @Override
    public Approval create(Approval approval) {

        if (approval == null || approval.getRequest() == null
                || approval.getRequest().getId() == null) {
            throw new RuntimeException("Request không được để trống");
        }

        Long requestId = approval.getRequest().getId();

        if (approvalRepository.existsByRequestId(requestId)) {
            throw new RuntimeException(
                    "Request này đã có Approval"
            );
        }

        RequestApprove request = requestApproveRepository.findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy yêu cầu với ID: " + requestId
                        )
                );

        Approval newApproval = new Approval();
        newApproval.setRequest(request);
        newApproval.setStatus("PENDING");
        newApproval.setStartedAt(LocalDateTime.now());
        newApproval.setCompletedAt(null);

        Approval savedApproval = approvalRepository.save(newApproval);

        createApprovalLines(savedApproval, request);

        return savedApproval;
    }

    // =====================================================
    // TẠO CÁC BƯỚC PHÊ DUYỆT
    // =====================================================

    private void createApprovalLines(
            Approval approval,
            RequestApprove request) {

        Employee employee = request.getEmployee();

        if (employee == null) {
            throw new RuntimeException(
                    "Yêu cầu chưa được liên kết với nhân viên"
            );
        }

        LocalDate today = LocalDate.now();

        EmployeeDepartment empDept =
                employeeDepartmentRepository
                        .findByEmployeeEmpIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                                employee.getEmpId(),
                                today,
                                today
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Nhân viên chưa được gán phòng ban"
                                )
                        );

        List<DepartmentApprover> approvers =
                departmentApproverRepository
                        .findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
                                empDept.getDepartment().getDeptId()
                        );

        if (approvers.isEmpty()) {
            throw new RuntimeException(
                    "Phòng ban chưa được cấu hình người phê duyệt"
            );
        }

        Integer previousOrder = null;

        for (int i = 0; i < approvers.size(); i++) {

            DepartmentApprover deptApprover = approvers.get(i);

            if (deptApprover.getEmployee() == null
                    || deptApprover.getApprovalOrder() == null) {
                throw new RuntimeException(
                        "Cấu hình người phê duyệt không hợp lệ"
                );
            }

            if (previousOrder != null
                    && previousOrder.equals(deptApprover.getApprovalOrder())) {
                throw new RuntimeException(
                        "Thứ tự phê duyệt bị trùng: "
                                + previousOrder
                );
            }

            previousOrder = deptApprover.getApprovalOrder();

            ApprovalLine line = new ApprovalLine();
            line.setApproval(approval);
            line.setApprover(deptApprover.getEmployee());
            line.setApprovalOrder(deptApprover.getApprovalOrder());

            // Bước đầu tiên được xử lý trước
            line.setStatus(i == 0 ? "PENDING" : "WAITING");

            approvalLineRepository.save(line);
        }
    }

    // =====================================================
    // APPROVE - PHÊ DUYỆT
    // =====================================================

    @Override
    public void approveLine(
            Long approvalLineId,
            Long actionEmpId,
            String comment) {

        ApprovalLine currentLine = approvalLineRepository.findById(
                approvalLineId
        ).orElseThrow(() ->
                new RuntimeException(
                        "Không tìm thấy bước phê duyệt"
                )
        );

        Approval approval = currentLine.getApproval();

        if (!"PENDING".equals(currentLine.getStatus())) {
            throw new RuntimeException(
                    "Bước phê duyệt này hiện không ở trạng thái chờ xử lý"
            );
        }

        if (!"PENDING".equals(approval.getStatus())) {
            throw new RuntimeException(
                    "Quy trình phê duyệt đã kết thúc"
            );
        }

        if (actionEmpId == null
                || currentLine.getApprover() == null
                || !actionEmpId.equals(
                        currentLine.getApprover().getEmpId()
                )) {
            throw new RuntimeException(
                    "Bạn không có quyền phê duyệt bước này"
            );
        }

        // Ghi nhận người phê duyệt
        currentLine.setStatus("APPROVED");
        currentLine.setApprovedAt(LocalDateTime.now());
        currentLine.setComment(comment);
        currentLine.setActionEmployee(currentLine.getApprover());

        approvalLineRepository.save(currentLine);

        // Tìm bước tiếp theo đang WAITING
        ApprovalLine nextLine =
                approvalLineRepository
                        .findFirstByApprovalIdAndApprovalOrderGreaterThanAndStatusOrderByApprovalOrderAsc(
                                approval.getId(),
                                currentLine.getApprovalOrder(),
                                "WAITING"
                        )
                        .orElse(null);

        if (nextLine != null) {

            // Tự động chuyển cấp tiếp theo
            nextLine.setStatus("PENDING");
            approvalLineRepository.save(nextLine);

        } else {

            // Không còn cấp nào: hoàn tất quy trình
            approval.setStatus("APPROVED");
            approval.setCompletedAt(LocalDateTime.now());

            approvalRepository.save(approval);
        }
    }

    // =====================================================
    // REJECT - TỪ CHỐI
    // =====================================================

    @Override
    public void rejectLine(
            Long approvalLineId,
            Long actionEmpId,
            String comment) {

        ApprovalLine currentLine = approvalLineRepository.findById(
                approvalLineId
        ).orElseThrow(() ->
                new RuntimeException(
                        "Không tìm thấy bước phê duyệt"
                )
        );

        Approval approval = currentLine.getApproval();

        if (!"PENDING".equals(currentLine.getStatus())
                || !"PENDING".equals(approval.getStatus())) {
            throw new RuntimeException(
                    "Bước phê duyệt không còn hiệu lực"
            );
        }

        if (actionEmpId == null
                || currentLine.getApprover() == null
                || !actionEmpId.equals(
                        currentLine.getApprover().getEmpId()
                )) {
            throw new RuntimeException(
                    "Bạn không có quyền từ chối bước này"
            );
        }

        currentLine.setStatus("REJECTED");
        currentLine.setApprovedAt(LocalDateTime.now());
        currentLine.setComment(comment);
        currentLine.setActionEmployee(currentLine.getApprover());

        approvalLineRepository.save(currentLine);

        // Hủy các bước đang chờ phía sau
        List<ApprovalLine> lines =
                approvalLineRepository
                        .findByApprovalIdOrderByApprovalOrderAsc(
                                approval.getId()
                        );

        for (ApprovalLine line : lines) {
            if ("WAITING".equals(line.getStatus())) {
                line.setStatus("CANCELLED");
                approvalLineRepository.save(line);
            }
        }

        approval.setStatus("REJECTED");
        approval.setCompletedAt(LocalDateTime.now());

        approvalRepository.save(approval);
    }

    // =====================================================
    // UPDATE
    // Không cho sửa trạng thái trực tiếp
    // =====================================================

    @Override
    public Approval update(Long id, Approval approval) {

        Approval existing = getById(id);

        if (approval.getStatus() != null
                && !approval.getStatus().equals(existing.getStatus())) {
            throw new RuntimeException(
                    "Không được cập nhật trực tiếp trạng thái Approval. "
                            + "Hãy sử dụng chức năng phê duyệt hoặc từ chối."
            );
        }

        // Trạng thái và completedAt chỉ được thay đổi
        // thông qua approveLine() hoặc rejectLine().
        return existing;
    }

    // =====================================================
    // CONVERT ENTITY -> DTO
    // =====================================================

    private ApprovalResponse toResponse(Approval approval) {

        ApprovalResponse response = new ApprovalResponse();

        response.setId(approval.getId());
        response.setStatus(approval.getStatus());
        response.setStartedAt(approval.getStartedAt());
        response.setCompletedAt(approval.getCompletedAt());

        RequestApprove request = approval.getRequest();

        if (request != null) {

            ApprovalResponse.RequestResponse requestResponse =
                    new ApprovalResponse.RequestResponse();

            requestResponse.setId(request.getId());
            requestResponse.setRequestCode(request.getRequestCode());
            requestResponse.setRequestType(request.getRequestType());
            requestResponse.setTitle(request.getTitle());
            requestResponse.setContent(request.getContent());
            requestResponse.setStatus(request.getStatus());
            requestResponse.setCreatedAt(request.getCreatedAt());
            requestResponse.setUpdatedAt(request.getUpdatedAt());

            Employee employee = request.getEmployee();

            if (employee != null) {

                ApprovalResponse.EmployeeResponse employeeResponse =
                        new ApprovalResponse.EmployeeResponse();

                employeeResponse.setEmpId(employee.getEmpId());
                employeeResponse.setEmpCode(employee.getEmpCode());
                employeeResponse.setEmpName(employee.getEmpName());
                employeeResponse.setEmail(employee.getEmail());
                employeeResponse.setActive(employee.getActive());

                requestResponse.setEmployee(employeeResponse);
            }

            response.setRequest(requestResponse);
        }

        return response;
    }
}
