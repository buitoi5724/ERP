
import { useEffect, useState, useRef } from "react";

import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Toast } from "primereact/toast";
import {
    ConfirmDialog,
    confirmDialog
} from "primereact/confirmdialog";
import { Toolbar } from "primereact/toolbar";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { Dropdown } from "primereact/dropdown";

import accountService from "./accountService";

import "primereact/resources/themes/lara-light-indigo/theme.css";
import "primereact/resources/primereact.min.css";
import "primeicons/primeicons.css";

import "./account.css";

export default function AccountComponent() {

    const [accounts, setAccounts] = useState([]);
    const [departments, setDepartments] = useState([]);

    const [selectedAccount, setSelectedAccount] = useState(null);
    const [selectedAccounts, setSelectedAccounts] = useState([]);

    const [formVisible, setFormVisible] = useState(false);
    const [globalSearch, setGlobalSearch] = useState("");
    const [loading, setLoading] = useState(false);

    const toast = useRef(null);

    // Role backend hiện có
    const roleOptions = [
        { label: "ADMIN", value: "ADMIN" },
        { label: "SALES", value: "SALES" },
        { label: "WAREHOUSE", value: "WAREHOUSE" },
        { label: "MANAGER", value: "MANAGER" },
        { label: "USER", value: "USER" }
    ];

    // Chức vụ nghiệp vụ
    const positionOptions = [
        { label: "Nhân viên", value: "EMPLOYEE" },
        { label: "Phó phòng", value: "DEPUTY_MANAGER" },
        { label: "Trưởng phòng", value: "DEPARTMENT_MANAGER" }
    ];

    const isEmployeeForm =
        selectedAccount?.isEmployee === true;

    // =====================================================
    // TOAST
    // =====================================================

    const toastError = (message) => {
        toast.current?.show({
            severity: "error",
            summary: "Lỗi",
            detail: message
        });
    };

    // =====================================================
    // LOAD ACCOUNTS
    // =====================================================

    const loadAccounts = async () => {
        try {
            setLoading(true);

            const data = await accountService.getAccounts();

            setAccounts(Array.isArray(data) ? data : []);

        } catch (error) {
            console.error("Load accounts error:", error);
            toastError("Không thể tải danh sách tài khoản");
        } finally {
            setLoading(false);
        }
    };

    // =====================================================
    // LOAD DEPARTMENTS
    // =====================================================

    const loadDepartments = async () => {
        try {
            const data = await accountService.getDepartments();

            setDepartments(
                Array.isArray(data)
                    ? data.filter(item => item.active !== false)
                    : []
            );
        } catch (error) {
            console.error("Load departments error:", error);
            toastError("Không thể tải danh sách phòng ban");
        }
    };

    useEffect(() => {
        loadAccounts();
        loadDepartments();
    }, []);

    // =====================================================
    // SEARCH
    // =====================================================

    const filteredAccounts = accounts.filter(account => {
        const keyword = globalSearch.trim().toLowerCase();

        if (!keyword) return true;

        return (
            account.username?.toLowerCase().includes(keyword) ||
            account.employee?.empCode?.toLowerCase().includes(keyword) ||
            account.employee?.empName?.toLowerCase().includes(keyword)
        );
    });

    // =====================================================
    // OPEN CREATE
    // =====================================================

    const openCreateDialog = () => {
        setSelectedAccount({
            isEmployee: true,

            username: "",
            password: "",
            status: true,

            empCode: "",
            empName: "",
            email: "",

            departmentId: null,
            position: "EMPLOYEE",

            // Role mặc định cho nhân viên
            role: "USER"
        });

        setFormVisible(true);
    };

    // =====================================================
    // OPEN EDIT
    // =====================================================

    const openEditDialog = (account) => {
        const currentRole =
            account.roles?.[0]?.name || "USER";

        setSelectedAccount({
            id: account.id,
            isEmployee: false,

            username: account.username,
            password: "",
            status: account.status,
            role: currentRole
        });

        setFormVisible(true);
    };

    // =====================================================
    // FORM CHANGE
    // =====================================================

  const updateField = (field, value) => {
    setSelectedAccount(previous => {
        const updated = {
            ...previous,
            [field]: value
        };

        // Tự động phân quyền theo chức vụ
        if (field === "position") {
            if (value === "EMPLOYEE") {
                updated.role = "USER";
            } else if (
                value === "DEPUTY_MANAGER" ||
                value === "DEPARTMENT_MANAGER"
            ) {
                updated.role = "MANAGER";
            }
        }

        return updated;
    });
};

    // =====================================================
    // VALIDATE
    // =====================================================

    const validateAccount = () => {
        const account = selectedAccount;

        if (!account?.username?.trim()) {
            toastError("Vui lòng nhập username");
            return false;
        }

        if (!account.id && !account.password?.trim()) {
            toastError("Vui lòng nhập mật khẩu");
            return false;
        }

        const usernameExists = accounts.some(item =>
            item.id !== account.id &&
            item.username?.toLowerCase() ===
                account.username.trim().toLowerCase()
        );

        if (usernameExists) {
            toastError("Username đã tồn tại");
            return false;
        }

        if (account.isEmployee) {
            if (!account.empCode?.trim()) {
                toastError("Vui lòng nhập mã nhân viên");
                return false;
            }

            if (!account.empName?.trim()) {
                toastError("Vui lòng nhập họ tên nhân viên");
                return false;
            }

            if (!account.email?.trim()) {
                toastError("Vui lòng nhập email");
                return false;
            }

            if (!account.departmentId) {
                toastError("Vui lòng chọn phòng ban");
                return false;
            }

            if (!account.position) {
                toastError("Vui lòng chọn chức vụ");
                return false;
            }
        } else if (!account.role) {
            toastError("Vui lòng chọn quyền");
            return false;
        }

        return true;
    };

    // =====================================================
    // SAVE
    // =====================================================

  const saveAccount = async () => {
    if (!validateAccount()) return;

    try {
        setLoading(true);

        if (selectedAccount.isEmployee) {

            // Xác định role trực tiếp từ position
            let role;

            switch (selectedAccount.position) {
                case "EMPLOYEE":
                    role = "USER";
                    break;

                case "DEPUTY_MANAGER":
                case "DEPARTMENT_MANAGER":
                    role = "MANAGER";
                    break;

                default:
                    toastError("Chức vụ không hợp lệ");
                    return;
            }

            const request = {
                username: selectedAccount.username.trim(),
                password: selectedAccount.password,
                role: role,
                status: selectedAccount.status,

                empCode: selectedAccount.empCode.trim(),
                empName: selectedAccount.empName.trim(),
                email: selectedAccount.email.trim(),

                departmentId: Number(selectedAccount.departmentId),
                position: selectedAccount.position
            };

            console.log("===== EMPLOYEE REQUEST =====");
            console.log("Username:", request.username);
            console.log("Position:", request.position);
            console.log("Role:", request.role);
            console.log("Department:", request.departmentId);
            console.log("Full data:", request);

            await accountService.createEmployeeAccount(request);

        } else {

            const request = {
                username: selectedAccount.username.trim(),
                password: selectedAccount.password || "",
                status: selectedAccount.status,
                role: selectedAccount.role
            };

            if (selectedAccount.id) {
                await accountService.updateAccount(
                    selectedAccount.id,
                    request
                );
            } else {
                await accountService.createAccount(request);
            }
        }

        toast.current?.show({
            severity: "success",
            summary: "Thành công",
            detail: selectedAccount.isEmployee
                ? "Tạo tài khoản nhân viên thành công"
                : "Lưu tài khoản thành công"
        });

        setFormVisible(false);
        setSelectedAccount(null);

        await loadAccounts();

    } catch (error) {
        console.error("Save account error:", error);
        console.error("Backend response:", error.response?.data);

        toastError(
            typeof error.response?.data === "string"
                ? error.response.data
                : error.response?.data?.message ||
                  "Không thể lưu tài khoản"
        );

    } finally {
        setLoading(false);
    }
};

    // =====================================================
    // DELETE ONE
    // =====================================================

    const removeAccount = (id) => {
        confirmDialog({
            message: "Bạn có chắc muốn xóa tài khoản này?",
            header: "Xác nhận xóa",
            icon: "pi pi-exclamation-triangle",
            acceptLabel: "Có",
            rejectLabel: "Không",

            accept: async () => {
                try {
                    await accountService.deleteAccount(id);

                    toast.current?.show({
                        severity: "success",
                        summary: "Thành công",
                        detail: "Đã xóa tài khoản"
                    });

                    await loadAccounts();
                } catch (error) {
                    console.error(error);
                    toastError(
                        "Không thể xóa tài khoản. Có thể tài khoản đang được liên kết với nhân viên."
                    );
                }
            }
        });
    };

    // =====================================================
    // DELETE SELECTED
    // =====================================================

    const removeSelectedAccounts = () => {
        if (!selectedAccounts.length) return;

        confirmDialog({
            message: `Bạn có chắc muốn xóa ${selectedAccounts.length} tài khoản đã chọn?`,
            header: "Xác nhận xóa",
            icon: "pi pi-exclamation-triangle",
            acceptLabel: "Có",
            rejectLabel: "Không",

            accept: async () => {
                try {
                    await Promise.all(
                        selectedAccounts.map(account =>
                            accountService.deleteAccount(account.id)
                        )
                    );

                    setSelectedAccounts([]);

                    toast.current?.show({
                        severity: "success",
                        summary: "Thành công",
                        detail: "Đã xóa các tài khoản đã chọn"
                    });

                    await loadAccounts();
                } catch (error) {
                    console.error(error);
                    toastError("Không thể xóa một số tài khoản đã chọn");
                    await loadAccounts();
                }
            }
        });
    };

    // =====================================================
    // ROLE DISPLAY
    // =====================================================

    const roleBodyTemplate = (row) => {
        if (!row.roles?.length) {
            return <span className="p-tag p-tag-secondary">Chưa có quyền</span>;
        }

        return (
            <div className="flex gap-1 flex-wrap">
                {row.roles.map(role => (
                    <span key={role.id} className="p-tag p-tag-info">
                        {role.name}
                    </span>
                ))}
            </div>
        );
    };

    // =====================================================
    // EMPLOYEE DISPLAY
    // =====================================================

    const employeeBodyTemplate = (row) => {
        if (!row.employee) return "-";

        return (
            <div>
                <div>{row.employee.empName}</div>
                <small>{row.employee.empCode}</small>
            </div>
        );
    };

    // =====================================================
    // STATUS DISPLAY
    // =====================================================

    const statusBodyTemplate = (row) => (
        <span className={
            row.status
                ? "p-tag p-tag-success"
                : "p-tag p-tag-danger"
        }>
            {row.status ? "Hoạt động" : "Khóa"}
        </span>
    );

    // =====================================================
    // ACTION
    // =====================================================

    const actionBodyTemplate = (row) => (
        <div className="flex gap-1">
            <Button
                icon="pi pi-pencil"
                rounded
                text
                severity="warning"
                tooltip="Chỉnh sửa"
                onClick={() => openEditDialog(row)}
            />

            <Button
                icon="pi pi-trash"
                rounded
                text
                severity="danger"
                tooltip="Xóa"
                onClick={() => removeAccount(row.id)}
            />
        </div>
    );

    // =====================================================
    // TOOLBAR
    // =====================================================

    const leftToolbarTemplate = () => (
        <Button
            label="Thêm nhân viên"
            icon="pi pi-user-plus"
            onClick={openCreateDialog}
        />
    );

    const rightToolbarTemplate = () => (
        <div className="flex align-items-center gap-2">
            <span className="p-input-icon-left">
                <i className="pi pi-search" />
                <InputText
                    placeholder="Tìm tài khoản, mã NV, họ tên..."
                    value={globalSearch}
                    onChange={e => setGlobalSearch(e.target.value)}
                />
            </span>

            <Button
                label="Xóa đã chọn"
                icon="pi pi-trash"
                severity="danger"
                disabled={!selectedAccounts.length}
                onClick={removeSelectedAccounts}
            />
        </div>
    );

    // =====================================================
    // UI
    // =====================================================

    return (
        <div className="p-4">
            <Toast ref={toast} />
            <ConfirmDialog />

            <h2>Quản lý tài khoản</h2>

            <Toolbar
                className="mb-3"
                start={leftToolbarTemplate}
                end={rightToolbarTemplate}
            />

            <DataTable
                value={filteredAccounts}
                loading={loading}
                paginator
                rows={10}
                selection={selectedAccounts}
                onSelectionChange={e => setSelectedAccounts(e.value)}
                selectionMode="checkbox"
                stripedRows
                responsiveLayout="scroll"
                emptyMessage="Không có tài khoản"
            >
                <Column
                    selectionMode="multiple"
                    headerStyle={{ width: "3rem" }}
                />

                <Column field="id" header="ID" sortable />

                <Column field="username" header="Username" sortable />

                <Column
                    header="Nhân viên"
                    body={employeeBodyTemplate}
                />

                <Column
                    header="Quyền"
                    body={roleBodyTemplate}
                />

                <Column
                    header="Trạng thái"
                    body={statusBodyTemplate}
                />

                <Column
                    header="Thao tác"
                    body={actionBodyTemplate}
                />
            </DataTable>

            {/* DIALOG */}

            <Dialog
                header={
                    selectedAccount?.id
                        ? "Chỉnh sửa tài khoản"
                        : "Tạo tài khoản nhân viên"
                }
                visible={formVisible}
                style={{ width: "min(95vw, 40rem)" }}
                modal
                onHide={() => {
                    setFormVisible(false);
                    setSelectedAccount(null);
                }}
            >
                {selectedAccount && (
                    <div className="p-fluid">

                        <h4>Thông tin đăng nhập</h4>

                        <div className="field">
                            <label>Username *</label>
                            <InputText
                                value={selectedAccount.username || ""}
                                onChange={e => updateField("username", e.target.value)}
                                disabled={!!selectedAccount.id}
                            />
                        </div>

                        <div className="field">
                            <label>Password *</label>
                            <InputText
                                type="password"
                                value={selectedAccount.password || ""}
                                placeholder={
                                    selectedAccount.id
                                        ? "Để trống nếu không đổi"
                                        : "Nhập mật khẩu"
                                }
                                onChange={e => updateField("password", e.target.value)}
                            />
                        </div>

                        {isEmployeeForm ? (
                            <>
                                <h4>Thông tin nhân viên</h4>

                                <div className="field">
                                    <label>Mã nhân viên *</label>
                                    <InputText
                                        value={selectedAccount.empCode || ""}
                                        onChange={e => updateField("empCode", e.target.value)}
                                    />
                                </div>

                                <div className="field">
                                    <label>Họ và tên *</label>
                                    <InputText
                                        value={selectedAccount.empName || ""}
                                        onChange={e => updateField("empName", e.target.value)}
                                    />
                                </div>

                                <div className="field">
                                    <label>Email *</label>
                                    <InputText
                                        type="email"
                                        value={selectedAccount.email || ""}
                                        onChange={e => updateField("email", e.target.value)}
                                    />
                                </div>

                                <div className="field">
                                    <label>Phòng ban *</label>
                                    <Dropdown
                                        value={selectedAccount.departmentId}
                                        options={departments.map(department => ({
                                            label: `${department.deptCode} - ${department.deptName}`,
                                            value: department.deptId
                                        }))}
                                        onChange={e => updateField("departmentId", e.value)}
                                        placeholder="Chọn phòng ban"
                                        filter
                                    />
                                </div>

                                <div className="field">
                                    <label>Chức vụ *</label>
                                    <Dropdown
                                        value={selectedAccount.position}
                                        options={positionOptions}
                                        onChange={e => updateField("position", e.value)}
                                        placeholder="Chọn chức vụ"
                                    />
                                </div>

                                <div className="field">
                                    <label>Trạng thái</label>
                                    <Dropdown
                                        value={selectedAccount.status}
                                        options={[
                                            { label: "Hoạt động", value: true },
                                            { label: "Khóa", value: false }
                                        ]}
                                        onChange={e => updateField("status", e.value)}
                                    />
                                </div>
                            </>
                        ) : (
                            <>
                                <div className="field">
                                    <label>Quyền</label>
                                    <Dropdown
                                        value={selectedAccount.role}
                                        options={roleOptions}
                                        onChange={e => updateField("role", e.value)}
                                        placeholder="Chọn quyền"
                                    />
                                </div>

                                <div className="field">
                                    <label>Trạng thái</label>
                                    <Dropdown
                                        value={selectedAccount.status}
                                        options={[
                                            { label: "Hoạt động", value: true },
                                            { label: "Khóa", value: false }
                                        ]}
                                        onChange={e => updateField("status", e.value)}
                                    />
                                </div>
                            </>
                        )}

                        <div className="flex justify-content-end gap-2 mt-4">
                            <Button
                                label="Hủy"
                                severity="secondary"
                                onClick={() => {
                                    setFormVisible(false);
                                    setSelectedAccount(null);
                                }}
                            />

                            <Button
                                label="Lưu"
                                icon="pi pi-check"
                                onClick={saveAccount}
                                loading={loading}
                            />
                        </div>
                    </div>
                )}
            </Dialog>
        </div>
    );
}
