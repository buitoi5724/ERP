
import React, {
    useEffect,
    useState,
    useContext,
    useMemo,
    useCallback
} from "react";

import { useNavigate, useParams } from "react-router-dom";

import { Button } from "primereact/button";
import { Tag } from "primereact/tag";

import ApprovalService from "./approvalService";
import ApprovalDetailAction from "./ApprovalDetailAction";

import { AuthContext } from "../auth/context/AuthContext";

import "./approval.css";

export default function ApprovalDetailPage() {

    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useContext(AuthContext);

    const [request, setRequest] = useState(null);
    const [approval, setApproval] = useState(null);
    const [approvalLines, setApprovalLines] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [cancelling, setCancelling] = useState(false);

    // =========================================================
    // ROLE
    // =========================================================

    const roles = useMemo(() => {

        const userRoles = Array.isArray(user?.roles)
            ? user.roles
            : [];

        return userRoles.map(role => {

            const value = typeof role === "object"
                ? role?.name || role?.authority || role?.roleName || ""
                : role;

            return String(value)
                .replace(/^ROLE_/i, "")
                .trim()
                .toUpperCase();

        });

    }, [user]);

    // Phó phòng có role MANAGER trong database
    const isApprover =
        roles.includes("MANAGER") ||
        roles.includes("DEPUTY_MANAGER") ||
        roles.includes("DEPARTMENT_MANAGER");

    // =========================================================
    // GET EMPLOYEE ID
    // =========================================================

    const getEmployeeId = (employee) => {

        if (!employee) return null;

        return (
            employee.empId ??
            employee.employeeId ??
            employee.emp_id ??
            employee.id ??
            null
        );

    };

    // ID nhân viên đang đăng nhập
    const currentEmployeeId =
        getEmployeeId(user?.employee) ??
        user?.empId ??
        user?.employeeId ??
        user?.emp_id ??
        null;

    // =========================================================
    // PERMISSION
    // =========================================================

    // Người đăng nhập có phải người tạo đơn không?
    const isOwner = useMemo(() => {

        if (!request || currentEmployeeId == null) {
            return false;
        }

        const requestEmployeeId = getEmployeeId(
            request.employee
        );

        return (
            requestEmployeeId != null &&
            String(requestEmployeeId) === String(currentEmployeeId)
        );

    }, [request, currentEmployeeId]);

    // =========================================================
    // LOAD DETAIL
    // =========================================================

    const fetchDetail = useCallback(async () => {

        try {

            setLoading(true);
            setError("");

            // 1. Lấy thông tin yêu cầu
            const requestData =
                await ApprovalService.getRequestById(id);

            setRequest(requestData);

            // 2. Lấy quy trình phê duyệt
            try {

                const approvalData =
                    await ApprovalService.getApprovalByRequestId(id);

                setApproval(approvalData);

                // 3. Lấy các bước phê duyệt
                if (approvalData?.id) {

                    const linesData =
                        await ApprovalService.getApprovalLines(
                            approvalData.id
                        );

                    const data = Array.isArray(linesData)
                        ? linesData
                        : linesData?.content ||
                          linesData?.data ||
                          [];

                    setApprovalLines(data);

                } else {

                    setApprovalLines([]);

                }

            } catch (approvalError) {

                console.warn(
                    "Request chưa có Approval:",
                    approvalError
                );

                setApproval(null);
                setApprovalLines([]);

            }

        } catch (err) {

            console.error(
                "Lỗi khi lấy chi tiết yêu cầu:",
                err
            );

            setError(
                err?.response?.data?.message ||
                "Không thể tải thông tin yêu cầu."
            );

            setRequest(null);

        } finally {

            setLoading(false);

        }

    }, [id]);

    useEffect(() => {

        if (id) {
            fetchDetail();
        }

    }, [id, fetchDetail]);

    // =========================================================
    // CHECK APPROVAL PERMISSION
    // =========================================================

    const canApproveLine = (line) => {

        // Chỉ phó phòng mới được duyệt
        if (!isApprover) {
            return false;
        }

        // Chỉ bước đang chờ duyệt
        if (line?.status !== "PENDING") {
            return false;
        }

        // Phải xác định được nhân viên đang đăng nhập
        if (currentEmployeeId == null) {
            return false;
        }

        // Người được giao duyệt
        const approverId = getEmployeeId(line?.approver);

        // Chưa có thông tin người duyệt thì không hiển thị nút
        if (approverId == null) {
            return false;
        }

        // Chỉ đúng người được giao mới được duyệt
        return (
            String(approverId) === String(currentEmployeeId)
        );

    };

    // =========================================================
    // CANCEL REQUEST
    // =========================================================

    const handleCancel = async () => {

        if (!request?.id || !isOwner) {
            return;
        }

        if (request.status !== "PENDING") {
            return;
        }

        const confirmed = window.confirm(
            `Bạn có chắc chắn muốn hủy đơn ${request.requestCode || ""} không?`
        );

        if (!confirmed) {
            return;
        }

        try {

            setCancelling(true);
            setError("");

            await ApprovalService.cancelRequest(request.id);

            alert("Hủy đơn thành công!");

            await fetchDetail();

        } catch (err) {

            console.error("Lỗi hủy đơn:", err);

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "Không thể hủy đơn."
            );

        } finally {

            setCancelling(false);

        }

    };

    // =========================================================
    // FORMAT DATE
    // =========================================================

    const formatDate = (value) => {

        if (!value) {
            return "—";
        }

        const date = new Date(value);

        return Number.isNaN(date.getTime())
            ? "—"
            : date.toLocaleString("vi-VN");

    };

    // =========================================================
    // STATUS LABEL
    // =========================================================

    const getStatusLabel = (status) => {

        const labels = {
            PENDING: "Chờ duyệt",
            WAITING: "Đang chờ",
            APPROVED: "Đã duyệt",
            REJECTED: "Từ chối",
            CANCELLED: "Đã hủy"
        };

        return labels[status] || status || "Không xác định";

    };

    // =========================================================
    // STATUS SEVERITY
    // =========================================================

    const getStatusSeverity = (status) => {

        const severity = {
            PENDING: "warning",
            WAITING: "secondary",
            APPROVED: "success",
            REJECTED: "danger",
            CANCELLED: "secondary"
        };

        return severity[status] || "info";

    };

    // =========================================================
    // EMPLOYEE NAME
    // =========================================================

    const getEmployeeName = (employee) => {

        if (!employee) {
            return "—";
        }

        return (
            employee.empName ||
            employee.fullName ||
            employee.name ||
            employee.empCode ||
            "—"
        );

    };

    // =========================================================
    // REQUEST TYPE LABEL
    // =========================================================

    const getRequestTypeLabel = (type) => {

        const types = {
            OM: "Đơn ốm",
            NGHI_PHEP: "Nghỉ phép",
            MUA_HANG: "Đề nghị mua hàng",
            THANH_TOAN: "Đề nghị thanh toán",
            XUAT_KHO: "Đề nghị xuất kho",
            KHAC: "Yêu cầu khác"
        };

        return types[type] || type || "—";

    };

    // =========================================================
    // LOADING
    // =========================================================

    if (loading) {

        return (

            <div className="approval-detail-page">

                <div className="approval-detail-loading">

                    <i className="pi pi-spin pi-spinner" />

                    <span>Đang tải thông tin...</span>

                </div>

            </div>

        );

    }

    // =========================================================
    // ERROR / NOT FOUND
    // =========================================================

    if (!request) {

        return (

            <div className="approval-detail-page">

                <Button
                    label="Quay lại"
                    icon="pi pi-arrow-left"
                    outlined
                    onClick={() => navigate("/approval")}
                />

                <div className="approval-detail-error">

                    <i className="pi pi-exclamation-triangle" />

                    <span>
                        {error || "Không tìm thấy yêu cầu."}
                    </span>

                </div>

            </div>

        );

    }

    // =========================================================
    // RENDER
    // =========================================================

    return (

        <div className="approval-detail-page">

            {/* HEADER */}

            <div className="approval-detail-header">

                <div>

                    <Button
                        label="Quay lại"
                        icon="pi pi-arrow-left"
                        text
                        onClick={() => navigate("/approval")}
                    />

                    <h1 className="approval-title">
                        Chi tiết yêu cầu
                    </h1>

                    <p className="approval-subtitle">
                        {request.requestCode}
                    </p>

                </div>

                <Tag
                    value={getStatusLabel(request.status)}
                    severity={getStatusSeverity(request.status)}
                />

            </div>

            {/* ERROR */}

            {error && (

                <div className="approval-form-error">

                    <i className="pi pi-exclamation-circle" />

                    <span>{error}</span>

                </div>

            )}

            {/* REQUEST INFORMATION */}

            <div className="approval-detail-card">

                <div className="approval-detail-card-header">

                    <div>
                        <i className="pi pi-file" />
                        <span>Thông tin yêu cầu</span>
                    </div>

                </div>

                <div className="approval-detail-grid">

                    <div className="approval-detail-field">

                        <label>Mã yêu cầu</label>

                        <strong>
                            {request.requestCode || "—"}
                        </strong>

                    </div>

                    <div className="approval-detail-field">

                        <label>Loại yêu cầu</label>

                        <strong>
                            {getRequestTypeLabel(request.requestType)}
                        </strong>

                    </div>

                    <div className="approval-detail-field">

                        <label>Người tạo</label>

                        <strong>
                            {getEmployeeName(request.employee)}
                        </strong>

                    </div>

                    <div className="approval-detail-field">

                        <label>Ngày tạo</label>

                        <strong>
                            {formatDate(request.createdAt)}
                        </strong>

                    </div>

                    <div className="approval-detail-field approval-detail-field-full">

                        <label>Tiêu đề</label>

                        <strong>
                            {request.title || "—"}
                        </strong>

                    </div>

                    <div className="approval-detail-field approval-detail-field-full">

                        <label>Nội dung</label>

                        <div className="approval-detail-content">
                            {request.content || "Không có nội dung."}
                        </div>

                    </div>

                </div>

                {/* CHỈ NGƯỜI TẠO ĐƠN MỚI ĐƯỢC HỦY */}

                {isOwner && request.status === "PENDING" && (

                    <div style={{
                        display: "flex",
                        justifyContent: "flex-end",
                        marginTop: "20px"
                    }}>

                        <Button
                            label="Hủy đơn"
                            icon="pi pi-times"
                            severity="danger"
                            outlined
                            loading={cancelling}
                            disabled={cancelling}
                            onClick={handleCancel}
                        />

                    </div>

                )}

            </div>

            {/* APPROVAL PROCESS */}

            <div className="approval-detail-card">

                <div className="approval-detail-card-header">

                    <div>

                        <i className="pi pi-sitemap" />

                        <span>Quy trình phê duyệt</span>

                    </div>

                    {approval && (

                        <Tag
                            value={getStatusLabel(approval.status)}
                            severity={getStatusSeverity(approval.status)}
                        />

                    )}

                </div>

                {!approval ? (

                    <div className="approval-detail-empty">

                        <i className="pi pi-info-circle" />

                        <span>
                            Chưa có quy trình phê duyệt.
                        </span>

                    </div>

                ) : (

                    <div className="approval-lines">

                        {approvalLines.length === 0 ? (

                            <div className="approval-detail-empty">

                                <i className="pi pi-info-circle" />

                                <span>
                                    Chưa có bước phê duyệt.
                                </span>

                            </div>

                        ) : (

                            approvalLines.map((line, index) => (

                                <div
                                    key={line.id}
                                    className={
                                        `approval-line ${
                                            line.status === "PENDING"
                                                ? "approval-line-current"
                                                : ""
                                        }`
                                    }
                                >

                                    <div className="approval-line-number">
                                        {index + 1}
                                    </div>

                                    <div className="approval-line-content">

                                        <div className="approval-line-top">

                                            <strong>
                                                {getEmployeeName(line.approver)}
                                            </strong>

                                            <Tag
                                                value={getStatusLabel(line.status)}
                                                severity={getStatusSeverity(line.status)}
                                            />

                                        </div>

                                        <div className="approval-line-info">

                                            <span>
                                                Bước {line.approvalOrder}
                                            </span>

                                            {line.approvedAt && (

                                                <span>
                                                    {formatDate(line.approvedAt)}
                                                </span>

                                            )}

                                        </div>

                                        {line.comment && (

                                            <div className="approval-line-comment">

                                                <i className="pi pi-comment" />

                                                <span>{line.comment}</span>

                                            </div>

                                        )}

                                        {line.actionEmployee && (

                                            <div className="approval-line-info">

                                                Người xử lý:{" "}

                                                {getEmployeeName(line.actionEmployee)}

                                            </div>

                                        )}

                                        {/* CHỈ ĐÚNG PHÓ PHÒNG ĐƯỢC GIAO MỚI THẤY */}

                                        {canApproveLine(line) && (

                                            <ApprovalDetailAction
                                                approvalLine={line}
                                                onSuccess={fetchDetail}
                                            />

                                        )}

                                    </div>

                                </div>

                            ))

                        )}

                    </div>

                )}

            </div>

        </div>

    );

}
