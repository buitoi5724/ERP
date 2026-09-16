
import React, { useEffect, useMemo, useState } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Tag } from "primereact/tag";

import ApprovalForm from "./ApprovalForm";
import ApprovalService from "./approvalService";
import "./approval.css";

export default function ApprovalPage() {

    // =========================
    // STATE
    // =========================

    const [requests, setRequests] = useState([]);
    const [searchText, setSearchText] = useState("");
    const [statusFilter, setStatusFilter] = useState("ALL");

    const [loading, setLoading] = useState(false);
    const [formVisible, setFormVisible] = useState(false);


    // =========================
    // LOAD DATA
    // =========================

    const fetchRequests = async () => {

        try {

            setLoading(true);

            const response =
                await ApprovalService.getAllRequests();

            const data = Array.isArray(response)
                ? response
                : response?.content || [];

            setRequests(data);

        } catch (error) {

            console.error(
                "Lỗi khi lấy danh sách yêu cầu phê duyệt:",
                error
            );

            setRequests([]);

        } finally {

            setLoading(false);

        }

    };


    useEffect(() => {
        fetchRequests();
    }, []);


    // =========================
    // FORM
    // =========================

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


    // =========================
    // FORMAT
    // =========================

    const formatDate = (value) => {

        if (!value) {
            return "—";
        }

        return new Date(value).toLocaleString("vi-VN");

    };


    const getEmployeeName = (employee) => {

        if (!employee) {
            return "—";
        }

        return (
            employee.fullName ||
            employee.name ||
            employee.empName ||
            employee.empCode ||
            "—"
        );

    };


    // =========================
    // STATUS
    // =========================

    const getStatusLabel = (status) => {

        const labels = {
            PENDING: "Chờ duyệt",
            APPROVED: "Đã duyệt",
            REJECTED: "Từ chối",
            CANCELLED: "Đã hủy"
        };

        return labels[status] || status || "Không xác định";

    };


    const getStatusSeverity = (status) => {

        const severity = {
            PENDING: "warning",
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


    // =========================
    // FILTER
    // =========================

    const filteredRequests = useMemo(() => {

        const keyword = searchText
            .trim()
            .toLowerCase();

        return requests.filter((item) => {

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

            return matchesSearch && matchesStatus;

        });

    }, [
        requests,
        searchText,
        statusFilter
    ]);


    // =========================
    // STATISTICS
    // =========================

    const statistics = useMemo(() => {

        return {
            total: requests.length,

            pending: requests.filter(
                item => item.status === "PENDING"
            ).length,

            approved: requests.filter(
                item => item.status === "APPROVED"
            ).length,

            rejected: requests.filter(
                item => item.status === "REJECTED"
            ).length
        };

    }, [requests]);


    // =========================
    // ACTION
    // =========================

    const handleDetail = (rowData) => {

        console.log(
            "Xem chi tiết request:",
            rowData.id
        );

        // Sau này chuyển sang:
        // navigate(`/approval/${rowData.id}`);

    };


    const actionBodyTemplate = (rowData) => (

        <Button
            icon="pi pi-eye"
            rounded
            outlined
            severity="info"
            tooltip="Xem chi tiết"
            onClick={() => handleDetail(rowData)}
        />

    );


    // =========================
    // RENDER
    // =========================

    return (

        <div className="approval-page">

            {/* =========================
                HEADER
            ========================= */}

            <div className="approval-header">

                <div>

                    <h1 className="approval-title">
                        Quản lý phê duyệt
                    </h1>

                    <p className="approval-subtitle">
                        Theo dõi và quản lý các yêu cầu phê duyệt
                    </p>

                </div>

                <Button
                    label="Tạo yêu cầu"
                    icon="pi pi-plus"
                    onClick={handleCreate}
                />

            </div>


            {/* =========================
                STATISTICS
            ========================= */}

            <div className="approval-statistics">

                <div className="approval-stat-card">

                    <div className="approval-stat-icon">
                        <i className="pi pi-file" />
                    </div>

                    <div>
                        <span>Tổng yêu cầu</span>
                        <strong>
                            {statistics.total}
                        </strong>
                    </div>

                </div>


                <div className="approval-stat-card">

                    <div className="approval-stat-icon">
                        <i className="pi pi-clock" />
                    </div>

                    <div>
                        <span>Chờ duyệt</span>
                        <strong>
                            {statistics.pending}
                        </strong>
                    </div>

                </div>


                <div className="approval-stat-card">

                    <div className="approval-stat-icon">
                        <i className="pi pi-check-circle" />
                    </div>

                    <div>
                        <span>Đã duyệt</span>
                        <strong>
                            {statistics.approved}
                        </strong>
                    </div>

                </div>


                <div className="approval-stat-card">

                    <div className="approval-stat-icon">
                        <i className="pi pi-times-circle" />
                    </div>

                    <div>
                        <span>Từ chối</span>
                        <strong>
                            {statistics.rejected}
                        </strong>
                    </div>

                </div>

            </div>


            {/* =========================
                TABLE
            ========================= */}

            <div className="approval-table-section">

                <div className="approval-table-header">

                    <div>

                        <h2>
                            Danh sách yêu cầu
                        </h2>

                        <span>
                            {filteredRequests.length} yêu cầu
                        </span>

                    </div>

                </div>


                {/* FILTER */}

                <div className="approval-filters">

                    <div className="approval-search">

                        <i className="pi pi-search" />

                        <input
                            type="text"
                            placeholder="Tìm mã, tiêu đề, loại yêu cầu..."
                            value={searchText}
                            onChange={(e) =>
                                setSearchText(e.target.value)
                            }
                        />

                    </div>


                    <select
                        className="approval-status-filter"
                        value={statusFilter}
                        onChange={(e) =>
                            setStatusFilter(e.target.value)
                        }
                    >

                        <option value="ALL">
                            Tất cả trạng thái
                        </option>

                        <option value="PENDING">
                            Chờ duyệt
                        </option>

                        <option value="APPROVED">
                            Đã duyệt
                        </option>

                        <option value="REJECTED">
                            Từ chối
                        </option>

                        <option value="CANCELLED">
                            Đã hủy
                        </option>

                    </select>

                </div>


                {/* DATA TABLE */}

                <DataTable
                    value={filteredRequests}
                    dataKey="id"
                    loading={loading}
                    stripedRows
                    paginator
                    rows={10}
                    rowsPerPageOptions={[10, 25, 50]}
                    responsiveLayout="scroll"
                    emptyMessage="Không có yêu cầu nào"
                >

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
                        header="Người tạo"
                        body={(rowData) =>
                            getEmployeeName(rowData.employee)
                        }
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
                        body={(rowData) =>
                            formatDate(rowData.createdAt)
                        }
                        sortable
                    />

                    <Column
                        header="Thao tác"
                        body={actionBodyTemplate}
                        style={{ width: "90px" }}
                    />

                </DataTable>

            </div>


            {/* =========================
                CREATE FORM
            ========================= */}

            <ApprovalForm
                visible={formVisible}
                onClose={handleCloseForm}
                onSuccess={handleFormSuccess}
            />

        </div>

    );

}

