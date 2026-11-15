
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import ApprovalService from "./approvalService";
import "./approval.css";


export default function ApprovalHistoryPage() {

    const navigate = useNavigate();

    const [history, setHistory] = useState([]);
    const [searchText, setSearchText] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [loading, setLoading] = useState(false);


    // =========================================================
    // LOAD HISTORY
    // =========================================================

    const fetchHistory = async () => {

        try {

            setLoading(true);

            const response =
                await ApprovalService.getMyApprovalHistory();

            const data = Array.isArray(response)
                ? response
                : response?.content || [];

            setHistory(data);

        } catch (error) {

            console.error(
                "Lỗi khi lấy lịch sử phê duyệt:",
                error
            );

            setHistory([]);

        } finally {

            setLoading(false);

        }

    };


    useEffect(() => {

        fetchHistory();

    }, []);


    // =========================================================
    // FORMAT DATE
    // =========================================================

    const formatDate = (value) => {

        if (!value) {
            return "—";
        }

        try {

            return new Date(value).toLocaleString("vi-VN");

        } catch {

            return "—";

        }

    };


    // =========================================================
    // STATUS
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


    const getStatusClass = (status) => {

        switch (status) {

            case "APPROVED":
                return "approval-status-approved";

            case "REJECTED":
                return "approval-status-rejected";

            case "PENDING":
                return "approval-status-pending";

            case "WAITING":
                return "approval-status-waiting";

            case "CANCELLED":
                return "approval-status-cancelled";

            default:
                return "approval-status-default";

        }

    };


    // =========================================================
    // EMPLOYEE
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
    // GET REQUEST
    // =========================================================

    const getRequest = (item) => {

        return (
            item?.approval?.request ||
            item?.approval?.requestApprove ||
            item?.request ||
            item?.requestApprove ||
            null
        );

    };


    // =========================================================
    // NORMALIZE
    // =========================================================

    const normalizedHistory = useMemo(() => {

        return history.map((line) => {

            const request = getRequest(line);

            return {

                ...line,

                request,

                requestCode:
                    request?.requestCode || "—",

                requestType:
                    request?.requestType || "—",

                title:
                    request?.title || "—",

                employee:
                    request?.employee || null,

                requestId:
                    request?.id || null,

                actionDate:
                    line?.approvedAt || null

            };

        });

    }, [history]);


    // =========================================================
    // FILTER
    // =========================================================

    const filteredHistory = useMemo(() => {

        const keyword =
            searchText.trim().toLowerCase();

        return normalizedHistory.filter((item) => {

            const employeeName =
                getEmployeeName(item.employee)
                    .toLowerCase();

            const matchesSearch =
                !keyword ||
                item.requestCode
                    ?.toLowerCase()
                    .includes(keyword) ||
                item.title
                    ?.toLowerCase()
                    .includes(keyword) ||
                item.requestType
                    ?.toLowerCase()
                    .includes(keyword) ||
                employeeName.includes(keyword);

            const matchesStatus =
                statusFilter === "ALL" ||
                item.status === statusFilter;

            return (
                matchesSearch &&
                matchesStatus
            );

        });

    }, [
        normalizedHistory,
        searchText,
        statusFilter
    ]);


    // =========================================================
    // STATISTICS
    // =========================================================

    const totalCount =
        normalizedHistory.length;

    const approvedCount =
        normalizedHistory.filter(
            item => item.status === "APPROVED"
        ).length;

    const rejectedCount =
        normalizedHistory.filter(
            item => item.status === "REJECTED"
        ).length;


    // =========================================================
    // DETAIL
    // =========================================================

    const handleDetail = (rowData) => {

        if (!rowData.requestId) {
            return;
        }

        navigate(
            `/approval/${rowData.requestId}`
        );

    };


    // =========================================================
    // RENDER
    // =========================================================

    return (

        <div className="approval-page">

            <div className="approval-container">


                {/* =================================================
                    HEADER
                ================================================= */}

                <div className="approval-header">

                    <div className="approval-header-left">

                        <div className="approval-header-icon">

                            <i className="pi pi-history" />

                        </div>

                        <div>

                            <h1 className="approval-title">
                                Lịch sử phê duyệt
                            </h1>

                            <p className="approval-subtitle">
                                Các yêu cầu bạn đã xử lý
                            </p>

                        </div>

                    </div>


                    <button
                        type="button"
                        className="approval-action-button"
                        onClick={() =>
                            navigate("/approval")
                        }
                    >

                        <i className="pi pi-arrow-left" />

                        <span>
                            Quay lại
                        </span>

                    </button>

                </div>


                {/* =================================================
                    CONTENT
                ================================================= */}

                <div className="approval-content">


                    {/* =================================================
                        STATISTICS
                    ================================================= */}

                    <div className="approval-statistics">


                        <div className="approval-stat-card">

                            <div className="approval-stat-icon">

                                <i className="pi pi-history" />

                            </div>

                            <div>

                                <span>
                                    Đã xử lý
                                </span>

                                <strong>
                                    {totalCount}
                                </strong>

                            </div>

                        </div>


                        <div className="approval-stat-card">

                            <div className="approval-stat-icon">

                                <i className="pi pi-check-circle" />

                            </div>

                            <div>

                                <span>
                                    Đã duyệt
                                </span>

                                <strong>
                                    {approvedCount}
                                </strong>

                            </div>

                        </div>


                        <div className="approval-stat-card">

                            <div className="approval-stat-icon">

                                <i className="pi pi-times-circle" />

                            </div>

                            <div>

                                <span>
                                    Từ chối
                                </span>

                                <strong>
                                    {rejectedCount}
                                </strong>

                            </div>

                        </div>

                    </div>


                    {/* =================================================
                        TABLE HEADER
                    ================================================= */}

                    <div className="approval-table-header">

                        <div>

                            <h2>
                                Lịch sử xử lý
                            </h2>

                            <span>
                                {filteredHistory.length} yêu cầu
                            </span>

                        </div>

                    </div>


                    {/* =================================================
                        FILTER
                    ================================================= */}

                    <div className="approval-filters">

                        <div className="approval-search">

                            <i className="pi pi-search" />

                            <input
                                type="text"
                                placeholder="Tìm mã, tiêu đề, người tạo..."
                                value={searchText}
                                onChange={(e) =>
                                    setSearchText(
                                        e.target.value
                                    )
                                }
                            />

                        </div>


                        <select
                            className="approval-status-filter"
                            value={statusFilter}
                            onChange={(e) =>
                                setStatusFilter(
                                    e.target.value
                                )
                            }
                        >

                            <option value="ALL">
                                Tất cả trạng thái
                            </option>

                            <option value="APPROVED">
                                Đã duyệt
                            </option>

                            <option value="REJECTED">
                                Từ chối
                            </option>

                        </select>

                    </div>


                    {/* =================================================
                        LOADING
                    ================================================= */}

                    {loading && (

                        <div className="approval-empty">
                            Đang tải lịch sử phê duyệt...
                        </div>

                    )}


                    {/* =================================================
                        EMPTY
                    ================================================= */}

                    {!loading &&
                        filteredHistory.length === 0 && (

                            <div className="approval-empty">

                                <i className="pi pi-history" />

                                <p>
                                    Chưa có lịch sử phê duyệt.
                                </p>

                            </div>

                        )}


                    {/* =================================================
                        TABLE
                    ================================================= */}

                    {!loading &&
                        filteredHistory.length > 0 && (

                            <div className="approval-table">

                                <table>

                                    <thead>

                                        <tr>

                                            <th>
                                                Mã yêu cầu
                                            </th>

                                            <th>
                                                Loại yêu cầu
                                            </th>

                                            <th>
                                                Tiêu đề
                                            </th>

                                            <th>
                                                Người tạo
                                            </th>

                                            <th>
                                                Kết quả
                                            </th>

                                            <th>
                                                Nhận xét
                                            </th>

                                            <th>
                                                Ngày xử lý
                                            </th>

                                            <th>
                                                Thao tác
                                            </th>

                                        </tr>

                                    </thead>


                                    <tbody>

                                        {filteredHistory.map(
                                            (rowData) => (

                                                <tr
                                                    key={
                                                        rowData.id
                                                    }
                                                >

                                                    <td>
                                                        {rowData.requestCode}
                                                    </td>


                                                    <td>
                                                        {rowData.requestType}
                                                    </td>


                                                    <td>
                                                        {rowData.title}
                                                    </td>


                                                    <td>
                                                        {
                                                            getEmployeeName(
                                                                rowData.employee
                                                            )
                                                        }
                                                    </td>


                                                    <td>

                                                        <span
                                                            className={
                                                                getStatusClass(
                                                                    rowData.status
                                                                )
                                                            }
                                                        >
                                                            {
                                                                getStatusLabel(
                                                                    rowData.status
                                                                )
                                                            }
                                                        </span>

                                                    </td>


                                                    <td>

                                                        {rowData.comment ? (

                                                            <span
                                                                title={
                                                                    rowData.comment
                                                                }
                                                            >
                                                                {
                                                                    rowData.comment
                                                                }
                                                            </span>

                                                        ) : (

                                                            "—"

                                                        )}

                                                    </td>


                                                    <td>
                                                        {
                                                            formatDate(
                                                                rowData.actionDate
                                                            )
                                                        }
                                                    </td>


                                                    <td>

                                                        <button
                                                            type="button"
                                                            className="approval-action-button"
                                                            onClick={() =>
                                                                handleDetail(
                                                                    rowData
                                                                )
                                                            }
                                                        >

                                                            <i className="pi pi-eye" />

                                                            <span>
                                                                Xem
                                                            </span>

                                                        </button>

                                                    </td>

                                                </tr>

                                            )
                                        )}

                                    </tbody>

                                </table>

                            </div>

                        )}

                </div>

            </div>

        </div>

    );

}
