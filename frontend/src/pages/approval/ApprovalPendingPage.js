
import React, {
    useCallback,
    useEffect,
    useMemo,
    useState
} from "react";

import { useNavigate } from "react-router-dom";

import ApprovalService from "./approvalService";
import "./approval.css";

export default function ApprovalPendingPage() {

    const navigate = useNavigate();

    const [pendingApprovals, setPendingApprovals] = useState([]);
    const [searchText, setSearchText] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    // =====================================================
    // NORMALIZE RESPONSE
    // =====================================================

    const getArray = (response) => {

        if (Array.isArray(response)) {
            return response;
        }

        if (Array.isArray(response?.data)) {
            return response.data;
        }

        if (Array.isArray(response?.content)) {
            return response.content;
        }

        if (Array.isArray(response?.data?.content)) {
            return response.data.content;
        }

        return [];

    };

    const getObject = (response) => {

        if (!response) {
            return null;
        }

        if (response?.data?.data) {
            return response.data.data;
        }

        if (response?.data && !Array.isArray(response.data)) {
            return response.data;
        }

        if (response?.data && Array.isArray(response.data)) {
            return null;
        }

        return response;

    };

    // =====================================================
    // GET REQUEST INFORMATION
    // =====================================================

    const getRequestInfo = (request) => {

        if (!request) {
            return {};
        }

        return {
            requestId: request.id ?? request.requestId ?? null,

            requestCode:
                request.requestCode ||
                request.request_code ||
                request.code ||
                "—",

            requestType:
                request.requestType ||
                request.requestTypeName ||
                request.type ||
                request.request_type ||
                "—",

            title:
                request.title ||
                request.subject ||
                request.name ||
                "—",

            content:
                request.content ||
                request.description ||
                request.reason ||
                "",

            employee:
                request.employee ||
                request.createdBy ||
                request.creator ||
                request.emp ||
                null,

            createdAt:
                request.createdAt ||
                request.createdDate ||
                request.created_at ||
                null,

            requestStatus:
                request.status || "—"
        };

    };

    // =====================================================
    // FIND REQUEST BY APPROVAL LINE
    // =====================================================

    const findRequestByApprovalLine = async (
        pendingLine,
        requests
    ) => {

        const lineId = Number(pendingLine.id);

        if (!lineId) {
            return null;
        }

        for (const request of requests) {

            const requestId = request?.id ?? request?.requestId;

            if (!requestId) {
                continue;
            }

            try {

                // Lấy quy trình phê duyệt theo ID đơn
                const approvalResponse =
                    await ApprovalService.getApprovalByRequestId(
                        requestId
                    );

                const approval = getObject(approvalResponse);

                if (!approval?.id) {
                    continue;
                }

                // Lấy các bước phê duyệt của quy trình
                const linesResponse =
                    await ApprovalService.getApprovalLines(
                        approval.id
                    );

                const approvalLines = getArray(linesResponse);

                // Tìm đúng approval_line.id
                const matchedLine = approvalLines.find(
                    item => Number(item.id) === lineId
                );

                if (matchedLine) {

                    return {
                        request,
                        approval,
                        matchedLine
                    };

                }

            } catch (err) {

                console.warn(
                    `Không thể kiểm tra đơn #${requestId}:`,
                    err
                );

            }

        }

        return null;

    };

    // =====================================================
    // LOAD PENDING APPROVALS
    // =====================================================

    const fetchPendingApprovals = useCallback(async () => {

        try {

            setLoading(true);
            setError("");

            // 1. Lấy các dòng chờ phê duyệt của tài khoản hiện tại
            const pendingResponse =
                await ApprovalService.getMyPendingRequests();

            const pendingLines = getArray(pendingResponse);

            if (pendingLines.length === 0) {

                setPendingApprovals([]);
                return;

            }

            // 2. Lấy danh sách đơn yêu cầu
            const requestResponse =
                await ApprovalService.getAllRequests();

            const requests = getArray(requestResponse);

            if (requests.length === 0) {

                setPendingApprovals(
                    pendingLines.map(line => ({
                        ...line,
                        approvalLineId: Number(line.id),
                        requestId: null,
                        requestCode: `Dòng phê duyệt #${line.id}`,
                        requestType: "—",
                        title: "Chưa lấy được thông tin đơn yêu cầu",
                        employee: null,
                        createdAt: null,
                        matched: false
                    }))
                );

                return;

            }

            // 3. Ghép từng dòng phê duyệt với đúng đơn
            const results = await Promise.all(

                pendingLines.map(async (line) => {

                    try {

                        const matched =
                            await findRequestByApprovalLine(
                                line,
                                requests
                            );

                        if (!matched) {

                            return {
                                ...line,
                                approvalLineId: Number(line.id),
                                requestId: null,
                                requestCode: `Dòng phê duyệt #${line.id}`,
                                requestType: "—",
                                title: "Không tìm thấy đơn yêu cầu",
                                employee: null,
                                createdAt: null,
                                matched: false
                            };

                        }

                        const requestInfo =
                            getRequestInfo(matched.request);

                        return {

                            ...line,

                            // ID của dòng phê duyệt
                            approvalLineId: Number(line.id),

                            // ID thực tế của đơn yêu cầu
                            requestId: requestInfo.requestId,

                            approval: {
                                ...matched.approval,
                                request: matched.request
                            },

                            request: matched.request,

                            requestCode: requestInfo.requestCode,
                            requestType: requestInfo.requestType,
                            title: requestInfo.title,
                            content: requestInfo.content,
                            employee: requestInfo.employee,
                            createdAt: requestInfo.createdAt,
                            requestStatus: requestInfo.requestStatus,

                            matched: true

                        };

                    } catch (err) {

                        console.error(
                            "Lỗi ghép dữ liệu dòng phê duyệt:",
                            line.id,
                            err
                        );

                        return {
                            ...line,
                            approvalLineId: Number(line.id),
                            requestId: null,
                            requestCode: `Dòng phê duyệt #${line.id}`,
                            requestType: "—",
                            title: "Lỗi lấy thông tin đơn",
                            employee: null,
                            createdAt: null,
                            matched: false
                        };

                    }

                })

            );

            setPendingApprovals(results);

        } catch (err) {

            console.error(
                "Lỗi tải danh sách chờ phê duyệt:",
                err
            );

            setPendingApprovals([]);

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "Không thể tải danh sách yêu cầu chờ phê duyệt."
            );

        } finally {

            setLoading(false);

        }

    }, []);

    // =====================================================
    // INITIAL LOAD
    // =====================================================

    useEffect(() => {

        fetchPendingApprovals();

    }, [fetchPendingApprovals]);

    // =====================================================
    // EMPLOYEE NAME
    // =====================================================

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

    // =====================================================
    // STATUS LABEL
    // =====================================================

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

    // =====================================================
    // STATUS CLASS
    // =====================================================

    const getStatusClass = (status) => {

        switch (status) {

            case "PENDING":
                return "approval-status-pending";

            case "WAITING":
                return "approval-status-waiting";

            case "APPROVED":
                return "approval-status-approved";

            case "REJECTED":
                return "approval-status-rejected";

            case "CANCELLED":
                return "approval-status-cancelled";

            default:
                return "approval-status-default";

        }

    };

    // =====================================================
    // FORMAT DATE
    // =====================================================

    const formatDate = (value) => {

        if (!value) {
            return "—";
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return "—";
        }

        return date.toLocaleString("vi-VN");

    };

    // =====================================================
    // SEARCH
    // =====================================================

    const filteredApprovals = useMemo(() => {

        const keyword = searchText.trim().toLowerCase();

        if (!keyword) {
            return pendingApprovals;
        }

        return pendingApprovals.filter(item => {

            const employeeName =
                getEmployeeName(item.employee).toLowerCase();

            return (
                String(item.requestCode || "")
                    .toLowerCase()
                    .includes(keyword) ||

                String(item.requestType || "")
                    .toLowerCase()
                    .includes(keyword) ||

                String(item.title || "")
                    .toLowerCase()
                    .includes(keyword) ||

                employeeName.includes(keyword)
            );

        });

    }, [pendingApprovals, searchText]);

    // =====================================================
    // OPEN REQUEST DETAIL
    // =====================================================

    const handleDetail = (rowData) => {

        if (!rowData.requestId) {

            setError(
                `Không tìm thấy đơn tương ứng với dòng phê duyệt #${rowData.approvalLineId}.`
            );

            return;

        }

        // Chuyển đến trang chi tiết đơn yêu cầu
        navigate(`/approval/${rowData.requestId}`);

    };

    // =====================================================
    // REFRESH
    // =====================================================

    const handleRefresh = () => {
        fetchPendingApprovals();
    };

    // =====================================================
    // RENDER
    // =====================================================

    return (

        <div className="approval-page">

            <div className="approval-container">

                {/* HEADER */}

                <div className="approval-header">

                    <div className="approval-header-left">

                        <div className="approval-header-icon">
                            <i className="pi pi-check-square" />
                        </div>

                        <div>

                            <h1 className="approval-title">
                                Chờ tôi phê duyệt
                            </h1>

                            <p className="approval-subtitle">
                                Các yêu cầu được phân công cho bạn xử lý
                            </p>

                        </div>

                    </div>

                    <div style={{
                        display: "flex",
                        gap: "10px",
                        flexWrap: "wrap"
                    }}>

                        <button
                            type="button"
                            className="approval-action-button"
                            onClick={handleRefresh}
                            disabled={loading}
                        >

                            <i className={`pi ${
                                loading
                                    ? "pi-spin pi-spinner"
                                    : "pi-refresh"
                            }`} />

                            <span>
                                Làm mới
                            </span>

                        </button>

                        <button
                            type="button"
                            className="approval-action-button"
                            onClick={() => navigate("/approval")}
                        >

                            <i className="pi pi-arrow-left" />

                            <span>
                                Quay lại
                            </span>

                        </button>

                    </div>

                </div>

                {/* CONTENT */}

                <div className="approval-content">

                    {/* STATISTICS */}

                    <div className="approval-statistics">

                        <div className="approval-stat-card">

                            <div className="approval-stat-icon">
                                <i className="pi pi-clock" />
                            </div>

                            <div>
                                <span>Chờ xử lý</span>
                                <strong>{pendingApprovals.length}</strong>
                            </div>

                        </div>

                        <div className="approval-stat-card">

                            <div className="approval-stat-icon">
                                <i className="pi pi-search" />
                            </div>

                            <div>
                                <span>Kết quả tìm kiếm</span>
                                <strong>{filteredApprovals.length}</strong>
                            </div>

                        </div>

                    </div>

                    {/* TABLE HEADER */}

                    <div className="approval-table-header">

                        <div>
                            <h2>Danh sách cần phê duyệt</h2>
                            <span>{filteredApprovals.length} yêu cầu</span>
                        </div>

                    </div>

                    {/* SEARCH */}

                    <div className="approval-filters">

                        <div className="approval-search">

                            <i className="pi pi-search" />

                            <input
                                type="text"
                                placeholder="Tìm mã, loại, tiêu đề, người tạo..."
                                value={searchText}
                                onChange={e => setSearchText(e.target.value)}
                            />

                        </div>

                    </div>

                    {/* ERROR */}

                    {!loading && error && (

                        <div className="approval-form-error">

                            <i className="pi pi-exclamation-circle" />
                            <span>{error}</span>

                        </div>

                    )}

                    {/* LOADING */}

                    {loading && (

                        <div className="approval-empty">

                            <i className="pi pi-spin pi-spinner" />

                            <p>Đang tải danh sách phê duyệt...</p>

                        </div>

                    )}

                    {/* EMPTY */}

                    {!loading &&
                        !error &&
                        filteredApprovals.length === 0 && (

                            <div className="approval-empty">

                                <i className="pi pi-check-circle" />

                                <p>
                                    {searchText
                                        ? "Không tìm thấy yêu cầu phù hợp."
                                        : "Hiện không có yêu cầu nào đang chờ bạn phê duyệt."
                                    }
                                </p>

                            </div>

                        )}

                    {/* TABLE */}

                    {!loading && filteredApprovals.length > 0 && (

                        <div className="approval-table">

                            <table>

                                <thead>

                                    <tr>
                                        <th>Mã yêu cầu</th>
                                        <th>Loại yêu cầu</th>
                                        <th>Tiêu đề</th>
                                        <th>Người tạo</th>
                                        <th>Trạng thái</th>
                                        <th>Ngày tạo</th>
                                        <th>Thao tác</th>
                                    </tr>

                                </thead>

                                <tbody>

                                    {filteredApprovals.map(rowData => (

                                        <tr key={rowData.approvalLineId}>

                                            <td>
                                                <strong>
                                                    {rowData.requestCode}
                                                </strong>
                                            </td>

                                            <td>{rowData.requestType}</td>

                                            <td>{rowData.title}</td>

                                            <td>
                                                {getEmployeeName(rowData.employee)}
                                            </td>

                                            <td>
                                                <span className={getStatusClass(rowData.status)}>
                                                    {getStatusLabel(rowData.status)}
                                                </span>
                                            </td>

                                            <td>
                                                {formatDate(rowData.createdAt)}
                                            </td>

                                            <td>

                                                <button
                                                    type="button"
                                                    className="approval-action-button"
                                                    disabled={!rowData.requestId}
                                                    onClick={() => handleDetail(rowData)}
                                                >

                                                    <i className="pi pi-eye" />
                                                    <span>Xem</span>

                                                </button>

                                                {!rowData.requestId && (
                                                    <small style={{
                                                        display: "block",
                                                        color: "#dc2626",
                                                        marginTop: "4px"
                                                    }}>
                                                        Chưa liên kết được đơn
                                                    </small>
                                                )}

                                            </td>

                                        </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    )}

                </div>

            </div>

        </div>

    );

}
