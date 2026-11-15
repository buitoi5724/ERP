
import React, {
    useContext,
    useEffect,
    useMemo,
    useState,
    useCallback
} from "react";

import { useNavigate } from "react-router-dom";

import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";

import ApprovalForm from "./ApprovalForm";
import ApprovalService from "./approvalService";

import { AuthContext } from "../auth/context/AuthContext";

import "./approval.css";

export default function ApprovalPage() {

    const navigate = useNavigate();
    const { user } = useContext(AuthContext);

    // =====================================================
    // ROLE
    // =====================================================

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

    const isAdmin = roles.includes("ADMIN");

    // Role MANAGER là role phó phòng trong dữ liệu hiện tại
    const isApprover =
        roles.includes("MANAGER") ||
        roles.includes("DEPUTY_MANAGER") ||
        roles.includes("DEPARTMENT_MANAGER");

    // =====================================================
    // STATE
    // =====================================================

    const [requests, setRequests] = useState([]);

    const [searchText, setSearchText] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const [formVisible, setFormVisible] = useState(false);

    const [cancellingId, setCancellingId] = useState(null);

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

    // =====================================================
    // LOAD REQUESTS
    // =====================================================

    const fetchRequests = useCallback(async () => {

        if (!user) {
            setRequests([]);
            return;
        }

        try {

            setLoading(true);
            setError("");

            // ADMIN: xem toàn bộ yêu cầu
            // NHÂN VIÊN / PHÓ PHÒNG: chỉ xem đơn do mình tạo
            const response = isAdmin
                ? await ApprovalService.getAllRequests()
                : await ApprovalService.getMyRequests();

            const data = getArray(response);

            setRequests(data);

        } catch (err) {

            console.error("Lỗi khi lấy danh sách đơn:", err);

            setRequests([]);

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "Không thể tải danh sách yêu cầu."
            );

        } finally {

            setLoading(false);

        }

    }, [user, isAdmin]);

    // =====================================================
    // EFFECT
    // =====================================================

    useEffect(() => {

        if (!user) {
            setRequests([]);
            return;
        }

        setStatusFilter("ALL");
        setSearchText("");

        fetchRequests();

    }, [user, fetchRequests]);

    // =====================================================
    // CREATE REQUEST
    // =====================================================

    const handleCreate = () => {
        setFormVisible(true);
    };

    const handleCloseForm = () => {
        setFormVisible(false);
    };

    const handleFormSuccess = async () => {

        setFormVisible(false);

        await fetchRequests();

    };

    // =====================================================
    // CANCEL REQUEST
    // =====================================================

    const handleCancel = async (rowData) => {

        if (!rowData?.id) {
            return;
        }

        if (rowData.status !== "PENDING") {
            return;
        }

        const confirmed = window.confirm(
            `Bạn có chắc chắn muốn hủy yêu cầu ${rowData.requestCode || ""} không?`
        );

        if (!confirmed) {
            return;
        }

        try {

            setCancellingId(rowData.id);
            setError("");

            await ApprovalService.cancelRequest(rowData.id);

            alert("Hủy yêu cầu thành công!");

            await fetchRequests();

        } catch (err) {

            console.error("Lỗi khi hủy yêu cầu:", err);

            setError(
                err?.response?.data?.message ||
                err?.response?.data?.error ||
                "Không thể hủy yêu cầu. Vui lòng thử lại."
            );

        } finally {

            setCancellingId(null);

        }

    };

    // =====================================================
    // DATE
    // =====================================================

    const formatDate = (value) => {

        if (!value) {
            return "—";
        }

        const date = new Date(value);

        return Number.isNaN(date.getTime())
            ? "—"
            : date.toLocaleString("vi-VN");

    };

    // =====================================================
    // STATUS
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

    const statusBodyTemplate = (rowData) => (

        <Tag
            value={getStatusLabel(rowData.status)}
            severity={getStatusSeverity(rowData.status)}
        />

    );

    // =====================================================
    // FILTER
    // =====================================================

    const filteredRequests = useMemo(() => {

        const keyword = searchText.trim().toLowerCase();

        return requests.filter(item => {

            const requestCode = String(
                item.requestCode || ""
            ).toLowerCase();

            const title = String(
                item.title || ""
            ).toLowerCase();

            const requestType = String(
                item.requestType || ""
            ).toLowerCase();

            const matchesSearch =
                !keyword ||
                requestCode.includes(keyword) ||
                title.includes(keyword) ||
                requestType.includes(keyword);

            const matchesStatus =
                statusFilter === "ALL" ||
                item.status === statusFilter;

            return matchesSearch && matchesStatus;

        });

    }, [requests, searchText, statusFilter]);

    // =====================================================
    // STATISTICS
    // =====================================================

    const totalRequests = requests.length;

    const pendingRequests = requests.filter(
        item => item.status === "PENDING"
    ).length;

    const approvedRequests = requests.filter(
        item => item.status === "APPROVED"
    ).length;

    const rejectedRequests = requests.filter(
        item => item.status === "REJECTED"
    ).length;

    // =====================================================
    // QUICK FILTER
    // =====================================================

    const handlePendingFilter = () => {
        setStatusFilter("PENDING");
    };

    const handleAllFilter = () => {
        setStatusFilter("ALL");
    };

    // =====================================================
    // NAVIGATION
    // =====================================================

    const handleDetail = (rowData) => {

        if (!rowData?.id) {
            return;
        }

        navigate(`/approval/${rowData.id}`);

    };

    const handlePendingPage = () => {

        if (!isApprover) {
            return;
        }

        navigate("/approval/pending");

    };

    const handleHistoryPage = () => {

        navigate("/approval/history");

    };

    const handleRefresh = () => {

        fetchRequests();

    };

    // =====================================================
    // EMPLOYEE NAME
    // =====================================================

    const employeeBodyTemplate = (rowData) => {

        const employee = rowData.employee;

        return (
            employee?.empName ||
            employee?.fullName ||
            employee?.name ||
            employee?.empCode ||
            "—"
        );

    };

    // =====================================================
    // ACTION
    // =====================================================

    const actionBodyTemplate = (rowData) => {

        const canCancel =
            !isAdmin &&
            rowData.status === "PENDING";

        return (

            <div style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                gap: "8px"
            }}>

                <Button
                    icon="pi pi-eye"
                    rounded
                    outlined
                    severity="info"
                    tooltip="Xem chi tiết"
                    tooltipOptions={{ position: "top" }}
                    onClick={() => handleDetail(rowData)}
                />

                {/* Nhân viên và phó phòng chỉ hủy đơn do mình tạo */}
                {canCancel && (

                    <Button
                        icon="pi pi-times"
                        rounded
                        outlined
                        severity="danger"
                        tooltip="Hủy yêu cầu"
                        tooltipOptions={{ position: "top" }}
                        loading={cancellingId === rowData.id}
                        disabled={cancellingId !== null}
                        onClick={() => handleCancel(rowData)}
                    />

                )}

            </div>

        );

    };

    // =====================================================
    // COMMON TABLE
    // =====================================================

    const renderRequestTable = () => (

        <div className="approval-table">

            <DataTable
                value={filteredRequests}
                dataKey="id"
                loading={loading}
                stripedRows
                paginator
                rows={10}
                rowsPerPageOptions={[10, 25, 50]}
                responsiveLayout="scroll"
                emptyMessage={
                    isAdmin
                        ? "Không có yêu cầu nào"
                        : "Bạn chưa có yêu cầu nào"
                }
            >

                {isAdmin && (
                    <Column
                        header="Người tạo"
                        body={employeeBodyTemplate}
                    />
                )}

                <Column
                    field="requestCode"
                    header="Mã yêu cầu"
                    sortable
                />

                <Column
                    field="requestType"
                    header="Loại yêu cầu"
                    sortable
                />

                <Column
                    field="title"
                    header="Tiêu đề"
                    sortable
                />

                <Column
                    field="status"
                    header="Trạng thái"
                    body={statusBodyTemplate}
                    sortable
                />

                <Column
                    field="createdAt"
                    header="Ngày tạo"
                    body={rowData => formatDate(rowData.createdAt)}
                    sortable
                />

                <Column
                    header="Thao tác"
                    body={actionBodyTemplate}
                    style={{
                        width: "120px",
                        textAlign: "center"
                    }}
                />

            </DataTable>

        </div>

    );

    // =====================================================
    // FILTER AREA
    // =====================================================

    const renderFilters = () => (

        <div className="approval-filters">

            <div className="approval-search">

                <i className="pi pi-search" />

                <input
                    type="text"
                    placeholder="Tìm mã, tiêu đề, loại yêu cầu..."
                    value={searchText}
                    onChange={e => setSearchText(e.target.value)}
                />

            </div>

            <select
                className="approval-status-filter"
                value={statusFilter}
                onChange={e => setStatusFilter(e.target.value)}
            >

                <option value="ALL">Tất cả trạng thái</option>
                <option value="PENDING">Chờ duyệt</option>
                <option value="WAITING">Đang chờ</option>
                <option value="APPROVED">Đã duyệt</option>
                <option value="REJECTED">Từ chối</option>
                <option value="CANCELLED">Đã hủy</option>

            </select>

        </div>

    );

    // =====================================================
    // STATISTICS AREA
    // =====================================================

    const renderStatistics = () => (

        <div className="approval-statistics">

            <div
                className="approval-stat-card"
                onClick={handleAllFilter}
                style={{ cursor: "pointer" }}
            >

                <div className="approval-stat-icon">
                    <i className="pi pi-file" />
                </div>

                <div>
                    <span>Tổng yêu cầu</span>
                    <strong>{totalRequests}</strong>
                </div>

            </div>

            <div
                className="approval-stat-card"
                onClick={handlePendingFilter}
                style={{ cursor: "pointer" }}
            >

                <div className="approval-stat-icon">
                    <i className="pi pi-clock" />
                </div>

                <div>
                    <span>Chờ duyệt</span>
                    <strong>{pendingRequests}</strong>
                </div>

            </div>

            <div className="approval-stat-card">

                <div className="approval-stat-icon">
                    <i className="pi pi-check-circle" />
                </div>

                <div>
                    <span>Đã duyệt</span>
                    <strong>{approvedRequests}</strong>
                </div>

            </div>

            <div className="approval-stat-card">

                <div className="approval-stat-icon">
                    <i className="pi pi-times-circle" />
                </div>

                <div>
                    <span>Từ chối</span>
                    <strong>{rejectedRequests}</strong>
                </div>

            </div>

        </div>

    );

    // =====================================================
    // ADMIN VIEW
    // =====================================================

    if (isAdmin) {

        return (

            <div className="approval-page">

                <div className="approval-container">

                    <div className="approval-header">

                        <div className="approval-header-left">

                            <div className="approval-header-icon">
                                <i className="pi pi-users" />
                            </div>

                            <div>

                                <h1 className="approval-title">
                                    Theo dõi phê duyệt
                                </h1>

                                <p className="approval-subtitle">
                                    Quản lý và theo dõi các yêu cầu trong hệ thống
                                </p>

                            </div>

                        </div>

                        <div style={{
                            display: "flex",
                            gap: "10px",
                            flexWrap: "wrap"
                        }}>

                            <Button
                                label="Lịch sử phê duyệt"
                                icon="pi pi-history"
                                outlined
                                onClick={handleHistoryPage}
                            />

                            <Button
                                label="Làm mới"
                                icon="pi pi-refresh"
                                outlined
                                onClick={handleRefresh}
                                loading={loading}
                            />

                        </div>

                    </div>

                    <div className="approval-content">

                        {renderStatistics()}

                        <div className="approval-table-header">

                            <div>

                                <h2>
                                    {statusFilter === "PENDING"
                                        ? "Đơn đang chờ duyệt"
                                        : "Tất cả yêu cầu"}
                                </h2>

                                <span>
                                    {filteredRequests.length} yêu cầu
                                </span>

                            </div>

                        </div>

                        {renderFilters()}

                        {error && (
                            <div className="approval-form-error">
                                <i className="pi pi-exclamation-circle" />
                                <span>{error}</span>
                            </div>
                        )}

                        {statusFilter === "PENDING" && (
                            <div className="approval-form-error">
                                <i className="pi pi-info-circle" />
                                <span>
                                    Đây là danh sách đơn đang chờ.
                                    Admin chỉ theo dõi, không mặc định có quyền phê duyệt.
                                </span>
                            </div>
                        )}

                        {renderRequestTable()}

                    </div>

                </div>

            </div>

        );

    }

    // =====================================================
    // EMPLOYEE / APPROVER VIEW
    // =====================================================

    return (

        <div className="approval-page">

            <div className="approval-container">

                <div className="approval-header">

                    <div className="approval-header-left">

                        <div className="approval-header-icon">
                            <i className="pi pi-file-check" />
                        </div>

                        <div>

                            <h1 className="approval-title">
                                Đơn của tôi
                            </h1>

                            <p className="approval-subtitle">
                                Theo dõi các yêu cầu do bạn tạo
                            </p>

                        </div>

                    </div>

                    <div style={{
                        display: "flex",
                        gap: "10px",
                        flexWrap: "wrap"
                    }}>

                        {/* Chỉ phó phòng mới có nút xem đơn chờ duyệt */}
                        {isApprover && (

                            <Button
                                label="Chờ tôi phê duyệt"
                                icon="pi pi-check-square"
                                severity="warning"
                                outlined
                                onClick={handlePendingPage}
                            />

                        )}

                        <Button
                            label="Tạo yêu cầu"
                            icon="pi pi-plus"
                            onClick={handleCreate}
                        />

                    </div>

                </div>

                <div className="approval-content">

                    {renderStatistics()}

                    <div className="approval-table-header">

                        <div>

                            <h2>Danh sách đơn của tôi</h2>

                            <span>
                                {filteredRequests.length} yêu cầu
                            </span>

                        </div>

                    </div>

                    {renderFilters()}

                    {error && (
                        <div className="approval-form-error">
                            <i className="pi pi-exclamation-circle" />
                            <span>{error}</span>
                        </div>
                    )}

                    {renderRequestTable()}

                </div>

            </div>

            <ApprovalForm
                visible={formVisible}
                onClose={handleCloseForm}
                onSuccess={handleFormSuccess}
            />

        </div>

    );

}
