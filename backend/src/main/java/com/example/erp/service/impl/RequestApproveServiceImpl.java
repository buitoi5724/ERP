
package com.example.erp.service.impl;
import java.util.Set;
import com.example.erp.dto.ApprovalLineResponse;
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
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
@Service
@Transactional
public class RequestApproveServiceImpl implements RequestApproveService {

	
	private static final Set<String> LEAVE_REQUEST_TYPES = Set.of(
	        "NGHI_PHEP",
	        "OM",
	        "NGHI_KHONG_LUONG",
	        "NGHI_VIEC_RIENG",
	        "NGHI_THAI_SAN",
	        "NGHI_BU"
	);
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

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getAll() {
        return requestApproveRepository.findAll();
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public RequestApprove getById(Long id) {
        return requestApproveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy yêu cầu với ID: " + id));
    }

    // GET BY REQUEST CODE
    @Override
    @Transactional(readOnly = true)
    public RequestApprove getByRequestCode(String requestCode) {
        return requestApproveRepository.findByRequestCode(requestCode)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy yêu cầu với mã: " + requestCode));
    }

    // GET BY EMPLOYEE
    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getByEmployee(Long empId) {
        return requestApproveRepository
                .findByEmployeeEmpIdOrderByCreatedAtDesc(empId);
    }

    // GET MY REQUESTS
    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getMyRequests() {

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        if (currentEmployee == null) {
            throw new RuntimeException(
                    "Tài khoản chưa được liên kết với Employee");
        }

        return requestApproveRepository
                .findByEmployeeEmpIdOrderByCreatedAtDesc(
                        currentEmployee.getEmpId());
    }

    // GET BY STATUS
    @Override
    @Transactional(readOnly = true)
    public List<RequestApprove> getByStatus(String status) {
        return requestApproveRepository
                .findByStatusOrderByCreatedAtDesc(status);
    }

    // GET PENDING APPROVALS
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalLineResponse> getPendingApprovals() {

        Employee currentEmployee =
                currentUserService.getCurrentEmployee();

        if (currentEmployee == null) {
            throw new RuntimeException(
                    "Tài khoản chưa được liên kết với Employee");
        }

        List<ApprovalLine> pendingLines =
                approvalLineRepository
                        .findByApproverEmpIdAndStatusAndApprovalRequestStatus(
                                currentEmployee.getEmpId(),
                                "PENDING",
                                "PENDING");

        return pendingLines.stream()
                .map(this::toApprovalLineResponse)
                .collect(Collectors.toList());
    }

    // MAP APPROVAL LINE TO DTO
    private ApprovalLineResponse toApprovalLineResponse(
            ApprovalLine line) {

        ApprovalLineResponse response =
                new ApprovalLineResponse();

        response.setId(line.getId());
        response.setApprovalOrder(line.getApprovalOrder());
        response.setStatus(line.getStatus());
        response.setApprovedAt(line.getApprovedAt());
        response.setComment(line.getComment());

        // Các thông tin Approval, Request và Employee
        // cần mapping theo đúng cấu trúc DTO thực tế.
        // Bổ sung sau khi đối chiếu ApprovalLineResponse.java.

        return response;
    }

    // CREATE REQUEST

@Override
public RequestApprove create(RequestApprove requestApprove) {

    // 1. Kiểm tra mã yêu cầu
    if (requestApprove.getRequestCode() == null
            || requestApprove.getRequestCode().trim().isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Mã yêu cầu không được để trống"
        );
    }

    // 2. Lấy nhân viên đăng nhập
    Employee currentEmployee = currentUserService.getCurrentEmployee();

    if (currentEmployee == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tài khoản chưa được liên kết với Employee"
        );
    }

    requestApprove.setEmployee(currentEmployee);

    // 3. Kiểm tra loại yêu cầu
    if (requestApprove.getRequestType() == null
            || requestApprove.getRequestType().trim().isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Loại yêu cầu không được để trống"
        );
    }

    String requestType = requestApprove.getRequestType().trim().toUpperCase();
    requestApprove.setRequestType(requestType);

    // 4. Kiểm tra tiêu đề
    if (requestApprove.getTitle() == null
            || requestApprove.getTitle().trim().isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tiêu đề không được để trống"
        );
    }

    // 5. Kiểm tra mã đơn trùng
    if (requestApproveRepository.existsByRequestCode(
            requestApprove.getRequestCode().trim())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Mã yêu cầu đã tồn tại: " + requestApprove.getRequestCode()
        );
    }
 // 6. Xác định loại đơn có thời gian nghỉ
    boolean isLeaveRequest = LEAVE_REQUEST_TYPES.contains(requestType);

    LocalDate startDate = requestApprove.getStartDate();
    LocalDate endDate = requestApprove.getEndDate();

    if (isLeaveRequest) {

        // 6.1. Bắt buộc nhập ngày nghỉ
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc nghỉ"
            );
        }

        // 6.2. Kiểm tra ngày bắt đầu và kết thúc
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc"
            );
        }

        // Không cấm thứ Bảy và Chủ nhật.
        // Số ngày phép thực tế sẽ được tính theo lịch làm việc doanh nghiệp.

    } else {

        // Các loại đơn chưa được cấu hình thời gian
        requestApprove.setStartDate(null);
        requestApprove.setEndDate(null);
    }

    // 7. Xác định phòng ban hiện tại của nhân viên
    LocalDate today = LocalDate.now();

    EmployeeDepartment employeeDepartment =
            employeeDepartmentRepository
                    .findByEmployeeEmpIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                            currentEmployee.getEmpId(),
                            today,
                            today
                    )
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Nhân viên chưa được gán phòng ban"
                    ));

    if (employeeDepartment.getDepartment() == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Nhân viên chưa được cấu hình phòng ban"
        );
    }

    Long deptId = employeeDepartment.getDepartment().getDeptId();

    // 8. Kiểm tra trùng lịch nghỉ trong cùng phòng ban
    if (isLeaveRequest) {

        List<RequestApprove> overlappingRequests =
                requestApproveRepository.findOverlappingLeaveRequests(
                        currentEmployee.getEmpId(),
                        deptId,
                        startDate,
                        endDate
                );

        if (!overlappingRequests.isEmpty()) {

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");

            StringBuilder message = new StringBuilder();

            message.append(
                    "Không thể gửi đơn vì thời gian nghỉ bị trùng "
                            + "với nhân viên khác trong phòng ban:"
            );

            for (RequestApprove existing : overlappingRequests) {

                Employee otherEmployee = existing.getEmployee();

                LocalDate overlapStart =
                        existing.getStartDate().isAfter(startDate)
                                ? existing.getStartDate()
                                : startDate;

                LocalDate overlapEnd =
                        existing.getEndDate().isBefore(endDate)
                                ? existing.getEndDate()
                                : endDate;

                message.append("\n\n- Nhân viên: ")
                        .append(otherEmployee.getEmpName())
                        .append(" (")
                        .append(otherEmployee.getEmpCode())
                        .append(")")

                        .append("\n  Loại đơn: ")
                        .append(existing.getRequestType())

                        .append("\n  Mã đơn: ")
                        .append(existing.getRequestCode())

                        .append("\n  Thời gian nghỉ: ")
                        .append(existing.getStartDate().format(formatter))
                        .append(" - ")
                        .append(existing.getEndDate().format(formatter))

                        .append("\n  Ngày bị trùng: ")
                        .append(overlapStart.format(formatter))
                        .append(" - ")
                        .append(overlapEnd.format(formatter));
            }

            message.append("\n\nVui lòng chọn khoảng thời gian khác.");

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    message.toString()
            );
        }
    }

    // 9. Lấy danh sách người duyệt của phòng ban
    List<DepartmentApprover> approvers =
            departmentApproverRepository
                    .findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
                            deptId
                    );

    if (approvers == null || approvers.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Phòng ban chưa cấu hình người phê duyệt"
        );
    }

    // 10. Lưu yêu cầu
    LocalDateTime now = LocalDateTime.now();

    requestApprove.setStatus("PENDING");
    requestApprove.setCreatedAt(now);
    requestApprove.setUpdatedAt(now);

    RequestApprove savedRequest =
            requestApproveRepository.save(requestApprove);

    // 11. Tạo Approval
 // 11. Tạo Approval
    Approval approval = new Approval();

    approval.setRequest(savedRequest);
    approval.setStatus("PENDING");
    approval.setStartedAt(LocalDateTime.now());

    Approval savedApproval = approvalRepository.save(approval);

    // 12. Tạo các ApprovalLine
    for (int i = 0; i < approvers.size(); i++) {

        DepartmentApprover departmentApprover = approvers.get(i);

        ApprovalLine approvalLine = new ApprovalLine();

        approvalLine.setApproval(savedApproval);
        approvalLine.setApprover(departmentApprover.getEmployee());
        approvalLine.setApprovalOrder(departmentApprover.getApprovalOrder());

        if (i == 0) {
            approvalLine.setStatus("PENDING");
        } else {
            approvalLine.setStatus("WAITING");
        }

        approvalLineRepository.save(approvalLine);
    }

    return savedRequest;
}


    // Tìm các ngày thứ Bảy và Chủ nhật
    private List<LocalDate> findWeekendDates(
            LocalDate startDate,
            LocalDate endDate) {

        List<LocalDate> weekends = new ArrayList<>();

        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {

            DayOfWeek day = date.getDayOfWeek();

            if (day == DayOfWeek.SATURDAY
                    || day == DayOfWeek.SUNDAY) {
                weekends.add(date);
            }

            date = date.plusDays(1);
        }

        return weekends;
    }

    // Hiển thị tên thứ bằng tiếng Việt
    private String getVietnameseDayName(LocalDate date) {

        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "Thứ Hai";
            case TUESDAY:
                return "Thứ Ba";
            case WEDNESDAY:
                return "Thứ Tư";
            case THURSDAY:
                return "Thứ Năm";
            case FRIDAY:
                return "Thứ Sáu";
            case SATURDAY:
                return "Thứ Bảy";
            case SUNDAY:
                return "Chủ nhật";
            default:
                return "";
        }
    }

    // UPDATE REQUEST
    @Override
    public RequestApprove update(
            Long id,
            RequestApprove requestApprove) {

        RequestApprove existing = getById(id);

        if (requestApprove.getRequestType() != null
                && !requestApprove.getRequestType().trim().isEmpty()) {
            existing.setRequestType(requestApprove.getRequestType());
        }

        if (requestApprove.getTitle() != null
                && !requestApprove.getTitle().trim().isEmpty()) {
            existing.setTitle(requestApprove.getTitle());
        }

        if (requestApprove.getContent() != null) {
            existing.setContent(requestApprove.getContent());
        }

        if (requestApprove.getStatus() != null
                && !requestApprove.getStatus().trim().isEmpty()) {
            existing.setStatus(requestApprove.getStatus());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        return requestApproveRepository.save(existing);
    }

    // CANCEL REQUEST
    @Override
    public RequestApprove cancel(Long id) {

        RequestApprove existing = getById(id);

        if ("APPROVED".equals(existing.getStatus())) {
            throw new RuntimeException(
                    "Không thể hủy yêu cầu đã được duyệt");
        }

        if ("REJECTED".equals(existing.getStatus())) {
            throw new RuntimeException(
                    "Không thể hủy yêu cầu đã bị từ chối");
        }

        
        if ("CANCELLED".equals(existing.getStatus())) {
            throw new RuntimeException(
                    "Yêu cầu đã được hủy trước đó");
        }

        existing.setStatus("CANCELLED");
        existing.setUpdatedAt(LocalDateTime.now());

        return requestApproveRepository.save(existing);
    }
}
