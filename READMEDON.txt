- Nhân viên: Tạo đơn phê duyệt: xin nghỉ phép, xin nghỉ không lương. đơn công tác, 
- Người phê duyệt:
	1. có 1 hoặc nhiều người, tùy vào từng phòng ban cấu hình.
	2. ủy quyền phê duyệt cho người khác phê duyệt dùm


Table:
1. RequestApprove: nơi lưu đơn, tạo đơn: FILE_ID, trạng thái đơn
2. EMPLOYEE: phân quyền nhân viên, user id
3. Approval: lưu thông tin phê duyệt
4. Approval Line: người nào phê duyệt
5. Department: phòng ban
6. Departmemt approve: thông tin người phê duyệt của phòng ban
7. CM_FILE: lưu thông tin file đính kèm cho đơn, có thể cho đính kèm nhiều file
8. CM_CODE: lưu thông tin cấu hình cho project: loại đơn, trạng thái đơn
9. lưu thông tin nguoìn được ủy quyền
10. employee_deparment: EMP_ID, DEPT_ID, thời gian ở phòng ban.99991231
11. mẫu đơn. 


Function:
1. tạo đơn, theo tiến trình đơn (đơn đã được ai phê duyệt, còn ai chưa phê duyệt)
2. gửi mail tới những người liên quan
3. phê duyệt đơn, từ chối đơn, người tạo hủy đơn.
4. cấu hình phòng ban, ai là người phê duyệt, cấu hình người ủy quyền
5. download file eexcel các đơn

src/main/java/com/example/erp/
│
├── controller/
│   │
│   ├── EmployeeController.java
│   ├── DepartmentController.java
│   ├── EmployeeDepartmentController.java
│   ├── DepartmentApproverController.java
│   ├── ApprovalDelegationController.java
│   ├── RequestApproveController.java
│   ├── ApprovalController.java
│   ├── ApprovalLineController.java
│   ├── CMFileController.java
│   ├── CMCodeController.java
│   └── RequestTemplateController.java
│
├── service/
│   │
│   ├── EmployeeService.java
│   ├── DepartmentService.java
│   ├── EmployeeDepartmentService.java
│   ├── DepartmentApproverService.java
│   ├── ApprovalDelegationService.java
│   ├── RequestApproveService.java
│   ├── ApprovalService.java
│   ├── ApprovalLineService.java
│   ├── CMFileService.java
│   ├── CMCodeService.java
│   └── RequestTemplateService.java
│
├── service/impl/
│   │
│   ├── EmployeeServiceImpl.java
│   ├── DepartmentServiceImpl.java
│   ├── EmployeeDepartmentServiceImpl.java
│   ├── DepartmentApproverServiceImpl.java
│   ├── ApprovalDelegationServiceImpl.java
│   ├── RequestApproveServiceImpl.java
│   ├── ApprovalServiceImpl.java
│   ├── ApprovalLineServiceImpl.java
│   ├── CMFileServiceImpl.java
│   ├── CMCodeServiceImpl.java
│   └── RequestTemplateServiceImpl.java
│
├── repository/
│   │
│   ├── EmployeeRepository.java
│   ├── DepartmentRepository.java
│   ├── EmployeeDepartmentRepository.java
│   ├── DepartmentApproverRepository.java
│   ├── ApprovalDelegationRepository.java
│   ├── RequestApproveRepository.java
│   ├── ApprovalRepository.java
│   ├── ApprovalLineRepository.java
│   ├── CMFileRepository.java
│   ├── CMCodeRepository.java
│   └── RequestTemplateRepository.java
│
└── entity/
    │
    ├── User.java
    ├── Employee.java
    ├── Department.java
    ├── EmployeeDepartment.java
    ├── DepartmentApprover.java
    ├── ApprovalDelegation.java
    ├── RequestApprove.java
    ├── Approval.java
    ├── ApprovalLine.java
    ├── CMFile.java
    ├── CMCode.java
    └── RequestTemplate.java
sửa lại phòng gửi dơn sale gửi đơn lên phó phòng ban rồi phó gửi lên trưởng để phê duyệt 
thêm lại phòng tạo tài khoản cho nhân viên vào phòng ban nào vào phòng ban đó 
rồi tạo trưởng phòng mặt định trong đó 
gửi gmail cho các nhân viên 
Thêm danh sách quản lý phòng ban có những ai và có chức vụ gì 
Và xem lịch xin nghĩ có bị trùng hay không nếu trùng 


