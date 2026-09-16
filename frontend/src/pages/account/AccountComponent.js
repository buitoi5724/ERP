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

    // =====================================================
    // STATE
    // =====================================================

    const [accounts, setAccounts] = useState([]);

    const [selectedAccount, setSelectedAccount] = useState(null);

    const [selectedAccounts, setSelectedAccounts] = useState([]);

    const [formVisible, setFormVisible] = useState(false);

    const [globalSearch, setGlobalSearch] = useState("");

    const [loading, setLoading] = useState(false);

    const toast = useRef(null);


    // =====================================================
    // ROLE OPTIONS
    // =====================================================

    const roleOptions = [
        {
            label: "ADMIN",
            value: "ADMIN"
        },
        {
            label: "SALES",
            value: "SALES"
        },
        {
            label: "WAREHOUSE",
            value: "WAREHOUSE"
        },
        {
            label: "MANAGER",
            value: "MANAGER"
        }
    ];


    // =====================================================
    // LOAD ACCOUNTS
    // =====================================================

    const loadAccounts = async () => {

        try {

            setLoading(true);

            const data =
                await accountService.getAccounts();

            console.log(
                "===== ACCOUNTS FROM API ====="
            );

            console.log(data);

            if (Array.isArray(data)) {

                data.forEach((account) => {

                    console.log(
                        account.username,
                        "=>",
                        account.roles
                    );

                });

            }

            setAccounts(
                Array.isArray(data)
                    ? data
                    : []
            );

        } catch (error) {

            console.error(
                "Load accounts error:",
                error
            );

            toast.current?.show({
                severity: "error",
                summary: "Lỗi",
                detail:
                    "Không thể tải danh sách tài khoản"
            });

        } finally {

            setLoading(false);

        }

    };


    // =====================================================
    // LOAD WHEN COMPONENT MOUNT
    // =====================================================

    useEffect(() => {

        loadAccounts();

    }, []);


    // =====================================================
    // SEARCH
    // =====================================================

    const filteredAccounts =
        accounts.filter((account) => {

            const keyword =
                globalSearch
                    .trim()
                    .toLowerCase();

            if (!keyword) {
                return true;
            }

            return (
                account.username
                    ?.toLowerCase()
                    .includes(keyword)
            );

        });


    // =====================================================
    // ERROR
    // =====================================================

    const toastError = (message) => {

        toast.current?.show({
            severity: "error",
            summary: "Lỗi",
            detail: message
        });

        return false;

    };


    // =====================================================
    // VALIDATE
    // =====================================================

    const validateAccount = (account) => {

        if (!account) {

            return toastError(
                "Không có dữ liệu tài khoản"
            );

        }


        if (!account.username?.trim()) {

            return toastError(
                "Vui lòng nhập username"
            );

        }


        // Tạo mới bắt buộc password
        if (
            !account.id &&
            !account.password?.trim()
        ) {

            return toastError(
                "Vui lòng nhập password"
            );

        }


        // Username không được trùng
        const usernameExists =
            accounts.some(
                (item) =>
                    item.id !== account.id &&
                    item.username ===
                        account.username
            );


        if (usernameExists) {

            return toastError(
                "Username đã tồn tại"
            );

        }


        // Phải có role
        if (!account.role) {

            return toastError(
                "Vui lòng chọn quyền"
            );

        }


        return true;

    };


    // =====================================================
    // SAVE ACCOUNT
    // =====================================================

    const saveAccount = async () => {

        if (
            !validateAccount(
                selectedAccount
            )
        ) {

            return;

        }


        try {

            setLoading(true);


            // =================================================
            // REQUEST
            // =================================================

            const request = {

                username:
                    selectedAccount.username,

                password:
                    selectedAccount.password || "",

                status:
                    selectedAccount.status,

                role:
                    selectedAccount.role

            };


            console.log(
                "===== ACCOUNT REQUEST ====="
            );

            console.log(request);


            // =================================================
            // UPDATE
            // =================================================

            if (selectedAccount.id) {

                console.log(
                    "===== UPDATE ACCOUNT ====="
                );

                console.log(
                    "USER ID:",
                    selectedAccount.id
                );

                console.log(
                    "ROLE:",
                    selectedAccount.role
                );


                await accountService.updateAccount(
                    selectedAccount.id,
                    request
                );

            }


            // =================================================
            // CREATE
            // =================================================

            else {

                console.log(
                    "===== CREATE ACCOUNT ====="
                );


                await accountService.createAccount(
                    request
                );

            }


            // =================================================
            // SUCCESS
            // =================================================

            toast.current?.show({
                severity: "success",
                summary: "Thành công",
                detail:
                    "Lưu tài khoản thành công"
            });


            // Đóng dialog
            setFormVisible(false);

            setSelectedAccount(null);


            // =================================================
            // QUAN TRỌNG
            // LẤY DỮ LIỆU MỚI TỪ BACKEND
            // =================================================

            await loadAccounts();

        } catch (error) {

            console.error(
                "Save account error:",
                error
            );

            console.error(
                "Response:",
                error.response?.data
            );


            toastError(
                error.response?.data?.message ||
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

            message:
                "Bạn có chắc muốn xóa tài khoản này?",

            header:
                "Xác nhận xóa",

            icon:
                "pi pi-exclamation-triangle",

            acceptLabel:
                "Có",

            rejectLabel:
                "Không",

            accept: async () => {

                try {

                    await accountService.deleteAccount(
                        id
                    );


                    toast.current?.show({
                        severity: "success",
                        summary: "Thành công",
                        detail:
                            "Đã xóa tài khoản"
                    });


                    await loadAccounts();

                } catch (error) {

                    console.error(
                        "Delete account error:",
                        error
                    );


                    toastError(
                        "Không thể xóa tài khoản"
                    );

                }

            }

        });

    };


    // =====================================================
    // DELETE SELECTED
    // =====================================================

    const removeSelectedAccounts = () => {

        if (
            !selectedAccounts.length
        ) {

            return;

        }


        confirmDialog({

            message:
                `Bạn có chắc muốn xóa ${selectedAccounts.length} tài khoản đã chọn?`,

            header:
                "Xác nhận xóa",

            icon:
                "pi pi-exclamation-triangle",

            acceptLabel:
                "Có",

            rejectLabel:
                "Không",

            accept: async () => {

                try {

                    await Promise.all(

                        selectedAccounts.map(
                            (account) =>
                                accountService.deleteAccount(
                                    account.id
                                )
                        )

                    );


                    setSelectedAccounts([]);


                    toast.current?.show({
                        severity: "success",
                        summary: "Thành công",
                        detail:
                            "Đã xóa các tài khoản đã chọn"
                    });


                    await loadAccounts();

                } catch (error) {

                    console.error(
                        "Delete selected error:",
                        error
                    );


                    toastError(
                        "Không thể xóa tài khoản"
                    );

                }

            }

        });

    };


    // =====================================================
    // CREATE
    // =====================================================

    const openCreateDialog = () => {

        setSelectedAccount({

            username: "",

            password: "",

            role: "SALES",

            status: true

        });


        setFormVisible(true);

    };


    // =====================================================
    // EDIT
    // =====================================================

    const openEditDialog = (account) => {

        // Backend:
        //
        // roles: [
        //     {
        //         id: 3,
        //         name: "SALES"
        //     }
        // ]
        //
        // Dropdown cần:
        //
        // "SALES"

        const currentRole =
            account.roles &&
            account.roles.length > 0
                ? account.roles[0].name
                : "SALES";


        console.log(
            "===== EDIT ACCOUNT ====="
        );

        console.log(
            "ID:",
            account.id
        );

        console.log(
            "USERNAME:",
            account.username
        );

        console.log(
            "ROLES:",
            account.roles
        );

        console.log(
            "CURRENT ROLE:",
            currentRole
        );


        setSelectedAccount({

            id:
                account.id,

            username:
                account.username,

            // Không đưa BCrypt password
            password:
                "",

            status:
                account.status,

            role:
                currentRole

        });


        setFormVisible(true);

    };


    // =====================================================
    // ROLE DISPLAY
    // =====================================================

    const roleBodyTemplate = (row) => {

        if (
            !row.roles ||
            row.roles.length === 0
        ) {

            return (

                <span
                    className="p-tag p-tag-secondary"
                >
                    Chưa có quyền
                </span>

            );

        }


        return (

            <div className="flex gap-1">

                {row.roles.map(
                    (role) => (

                        <span
                            key={role.id}
                            className="p-tag p-tag-info"
                        >
                            {role.name}
                        </span>

                    )
                )}

            </div>

        );

    };


    // =====================================================
    // STATUS DISPLAY
    // =====================================================

    const statusBodyTemplate = (row) => {

        if (row.status) {

            return (

                <span
                    className="p-tag p-tag-success"
                >
                    Hoạt động
                </span>

            );

        }


        return (

            <span
                className="p-tag p-tag-danger"
            >
                Khóa
            </span>

        );

    };


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
                onClick={() =>
                    openEditDialog(row)
                }
            />


            <Button
                icon="pi pi-trash"
                rounded
                text
                severity="danger"
                tooltip="Xóa"
                onClick={() =>
                    removeAccount(row.id)
                }
            />

        </div>

    );


    // =====================================================
    // TOOLBAR LEFT
    // =====================================================

    const leftToolbarTemplate = () => (

        <Button
            label="Thêm tài khoản"
            icon="pi pi-plus"
            onClick={
                openCreateDialog
            }
        />

    );


    // =====================================================
    // TOOLBAR RIGHT
    // =====================================================

    const rightToolbarTemplate = () => (

        <div
            className="flex align-items-center gap-2"
        >

            <span
                className="p-input-icon-left"
            >

                <i className="pi pi-search" />


                <InputText
                    placeholder="Tìm username..."
                    value={globalSearch}
                    onChange={(e) =>
                        setGlobalSearch(
                            e.target.value
                        )
                    }
                />

            </span>


            <Button
                label="Xóa đã chọn"
                icon="pi pi-trash"
                severity="danger"
                disabled={
                    !selectedAccounts.length
                }
                onClick={
                    removeSelectedAccounts
                }
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


            <h2>
                Quản lý tài khoản
            </h2>


            <Toolbar
                className="mb-3"
                start={
                    leftToolbarTemplate
                }
                end={
                    rightToolbarTemplate
                }
            />


            {/* =================================================
                TABLE
            ================================================= */}

            <DataTable

                value={
                    filteredAccounts
                }

                loading={
                    loading
                }

                paginator

                rows={10}

                selection={
                    selectedAccounts
                }

                onSelectionChange={(e) =>
                    setSelectedAccounts(
                        e.value
                    )
                }

                selectionMode="checkbox"

                stripedRows

                responsiveLayout="scroll"

                emptyMessage={
                    "Không có tài khoản"
                }

            >


                <Column
                    selectionMode="multiple"
                    headerStyle={{
                        width: "3rem"
                    }}
                />


                <Column
                    field="id"
                    header="ID"
                    sortable
                />


                <Column
                    field="username"
                    header="Username"
                    sortable
                />


                <Column
                    header="Quyền"
                    body={
                        roleBodyTemplate
                    }
                />


                <Column
                    header="Trạng thái"
                    body={
                        statusBodyTemplate
                    }
                />


                <Column
                    header="Thao tác"
                    body={
                        actionBodyTemplate
                    }
                />

            </DataTable>


            {/* =================================================
                EDIT / CREATE DIALOG
            ================================================= */}

            <Dialog

                header={
                    selectedAccount?.id
                        ? "Chỉnh sửa tài khoản"
                        : "Thêm tài khoản"
                }

                visible={
                    formVisible
                }

                style={{
                    width: "30rem"
                }}

                modal

                onHide={() => {

                    setFormVisible(false);

                    setSelectedAccount(
                        null
                    );

                }}

            >

                {selectedAccount && (

                    <div className="p-fluid">


                        {/* =====================================
                            USERNAME
                        ===================================== */}

                        <div className="field">

                            <label>
                                Username
                            </label>


                            <InputText

                                value={
                                    selectedAccount.username ||
                                    ""
                                }

                                onChange={(e) =>
                                    setSelectedAccount({
                                        ...selectedAccount,

                                        username:
                                            e.target.value
                                    })
                                }

                                disabled={
                                    !!selectedAccount.id
                                }

                            />

                        </div>


                        {/* =====================================
                            PASSWORD
                        ===================================== */}

                        <div className="field">

                            <label>
                                Password
                            </label>


                            <InputText

                                type="password"

                                value={
                                    selectedAccount.password ||
                                    ""
                                }

                                placeholder={
                                    selectedAccount.id
                                        ? "Để trống nếu không đổi"
                                        : "Nhập password"
                                }

                                onChange={(e) =>
                                    setSelectedAccount({
                                        ...selectedAccount,

                                        password:
                                            e.target.value
                                    })
                                }

                            />

                        </div>


                        {/* =====================================
                            ROLE
                        ===================================== */}

                        <div className="field">

                            <label>
                                Quyền
                            </label>


                            <Dropdown

                                value={
                                    selectedAccount.role
                                }

                                options={
                                    roleOptions
                                }

                                onChange={(e) => {

                                    console.log(
                                        "ROLE SELECTED:",
                                        e.value
                                    );


                                    setSelectedAccount({
                                        ...selectedAccount,

                                        role:
                                            e.value
                                    });

                                }}

                                placeholder={
                                    "Chọn quyền"
                                }

                            />

                        </div>


                        {/* =====================================
                            STATUS
                        ===================================== */}

                        <div className="field">

                            <label>
                                Trạng thái
                            </label>


                            <Dropdown

                                value={
                                    selectedAccount.status
                                }

                                options={[
                                    {
                                        label:
                                            "Hoạt động",
                                        value:
                                            true
                                    },
                                    {
                                        label:
                                            "Khóa",
                                        value:
                                            false
                                    }
                                ]}

                                onChange={(e) =>
                                    setSelectedAccount({
                                        ...selectedAccount,

                                        status:
                                            e.value
                                    })
                                }

                            />

                        </div>


                        {/* =====================================
                            BUTTON
                        ===================================== */}

                        <div
                            className="flex justify-content-end gap-2 mt-4"
                        >

                            <Button
                                label="Hủy"
                                severity="secondary"
                                onClick={() => {

                                    setFormVisible(
                                        false
                                    );

                                    setSelectedAccount(
                                        null
                                    );

                                }}
                            />


                            <Button
                                label="Lưu"
                                icon="pi pi-check"
                                onClick={
                                    saveAccount
                                }
                                loading={
                                    loading
                                }
                            />

                        </div>

                    </div>

                )}

            </Dialog>

        </div>

    );

}