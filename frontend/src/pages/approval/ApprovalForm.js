import React, { useEffect, useMemo, useRef, useState } from "react";

import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { Dropdown } from "primereact/dropdown";
import { InputNumber } from "primereact/inputnumber";
import { Button } from "primereact/button";
import { FileUpload } from "primereact/fileupload";
import { Divider } from "primereact/divider";
import { Toast } from "primereact/toast";

import ApprovalService from "./approvalService";
import "./ApprovalForm.css";

// Danh sách loại yêu cầu
const REQUEST_TYPES = [
    { label: "Nghỉ ốm", value: "OM", icon: "pi pi-heart" },
    { label: "Nghỉ phép năm", value: "NGHI_PHEP", icon: "pi pi-calendar" },
    { label: "Nghỉ không hưởng lương", value: "NGHI_KHONG_LUONG", icon: "pi pi-wallet" },
    { label: "Nghỉ việc riêng", value: "NGHI_VIEC_RIENG", icon: "pi pi-user" },
    { label: "Nghỉ thai sản", value: "NGHI_THAI_SAN", icon: "pi pi-users" },
    { label: "Nghỉ bù", value: "NGHI_BU", icon: "pi pi-clock" },
    { label: "Đề nghị mua hàng", value: "MUA_HANG", icon: "pi pi-shopping-cart" },
    { label: "Đề nghị thanh toán", value: "THANH_TOAN", icon: "pi pi-money-bill" },
    { label: "Đề nghị xuất kho", value: "XUAT_KHO", icon: "pi pi-box" },
    { label: "Yêu cầu khác", value: "KHAC", icon: "pi pi-file" }
];

// Các loại đơn cần nhập thời gian nghỉ
const LEAVE_TYPES = [
    "NGHI_PHEP",
    "OM",
    "NGHI_KHONG_LUONG",
    "NGHI_VIEC_RIENG",
    "NGHI_THAI_SAN",
    "NGHI_BU"
];

// Thông báo khi đơn nghỉ bị trùng
const CONFLICT_MESSAGE =
    "Đơn của bạn đã bị trùng ngày nghỉ với nhân viên khác rồi. Vui lòng chuyển ngày nghỉ sang ngày khác.";

// Khởi tạo form
const createForm = () => ({
    requestCode: `REQ-${new Date().getFullYear()}-${Math.floor(1000 + Math.random() * 9000)}`,
    requestType: "",
    title: "",
    content: "",
    startDate: "",
    endDate: "",
    itemName: "",
    quantity: null,
    amount: null,
    reason: ""
});

// Lấy thông báo lỗi từ backend
const getErrorMessage = (err) => {
    const data = err?.response?.data;

    if (typeof data === "string") {
        return data;
    }

    return (
        data?.message ||
        data?.error ||
        data?.detail ||
        err?.message ||
        "Không thể tạo yêu cầu phê duyệt."
    );
};

export default function ApprovalForm({ visible, onClose, onSuccess }) {
    const toastRef = useRef(null);

    const [formData, setFormData] = useState(createForm);
    const [files, setFiles] = useState([]);
    const [loading, setLoading] = useState(false);

    // Hiển thị thông báo
    const showToast = (severity, summary, detail, life = 5000) => {
        toastRef.current?.show({
            severity,
            summary,
            detail,
            life,
            closable: true
        });
    };

    // Thông báo lỗi trùng lịch
    const showConflictToast = () => {
        toastRef.current?.show({
            severity: "error",
            summary: "Đơn nghỉ bị trùng lịch!",
            detail: CONFLICT_MESSAGE,
            life: 6000,
            closable: true,
            sticky: false
        });
    };

    // Reset form khi mở Dialog
    useEffect(() => {
        if (visible) {
            setFormData(createForm());
            setFiles([]);
        }
    }, [visible]);

    // Cập nhật dữ liệu form
    const change = (field, value) => {
        setFormData(prev => ({
            ...prev,
            [field]: value
        }));
    };

    // Loại yêu cầu đang chọn
    const selectedType = useMemo(
        () => REQUEST_TYPES.find(x => x.value === formData.requestType),
        [formData.requestType]
    );

    // Kiểm tra loại đơn nghỉ
    const isLeave = LEAVE_TYPES.includes(formData.requestType);

    // Tính tổng ngày nghỉ theo lịch, bao gồm cuối tuần
    const totalDays = useMemo(() => {
        if (!formData.startDate || !formData.endDate) {
            return 0;
        }

        const start = new Date(`${formData.startDate}T00:00:00`);
        const end = new Date(`${formData.endDate}T00:00:00`);

        if (end < start) {
            return 0;
        }

        return Math.round(
            (end - start) / (1000 * 60 * 60 * 24)
        ) + 1;
    }, [formData.startDate, formData.endDate]);

    // Kiểm tra dữ liệu trước khi gửi
    const validate = () => {
        const {
            requestType,
            title,
            startDate,
            endDate,
            reason,
            itemName,
            quantity,
            amount
        } = formData;

        if (!requestType) {
            return "Vui lòng chọn loại yêu cầu.";
        }

        if (!title.trim()) {
            return "Vui lòng nhập tiêu đề yêu cầu.";
        }

        if (isLeave) {
            if (!startDate) {
                return "Vui lòng chọn ngày bắt đầu.";
            }

            if (!endDate) {
                return "Vui lòng chọn ngày kết thúc.";
            }

            if (endDate < startDate) {
                return "Ngày kết thúc không được trước ngày bắt đầu.";
            }

            if (!reason.trim()) {
                return "Vui lòng nhập lý do nghỉ.";
            }
        }

        if (requestType === "MUA_HANG") {
            if (!itemName.trim()) {
                return "Vui lòng nhập tên mặt hàng.";
            }

            if (!quantity || quantity <= 0) {
                return "Số lượng phải lớn hơn 0.";
            }
        }

        if (requestType === "THANH_TOAN") {
            if (!amount || amount <= 0) {
                return "Vui lòng nhập số tiền cần thanh toán.";
            }

            if (!reason.trim()) {
                return "Vui lòng nhập nội dung thanh toán.";
            }
        }

        return null;
    };

    // Đóng form
    const handleClose = () => {
        if (loading) {
            return;
        }

        setFormData(createForm());
        setFiles([]);
        onClose?.();
    };

    // Gửi yêu cầu
    const handleSubmit = async () => {
        const validationError = validate();

        if (validationError) {
            showToast(
                "warn",
                "Thông tin chưa hợp lệ!",
                validationError
            );
            return;
        }

        try {
            setLoading(true);

            const payload = {
                requestCode: formData.requestCode,
                requestType: formData.requestType,
                title: formData.title.trim(),
                content: formData.content.trim(),
                startDate: isLeave ? formData.startDate : null,
                endDate: isLeave ? formData.endDate : null,
                itemName: formData.itemName.trim() || null,
                quantity: formData.quantity || null,
                amount: formData.amount || null,
                reason: formData.reason.trim() || null
            };

            console.log("========== GỬI YÊU CẦU ==========");
            console.log("Payload:", payload);
            console.log("Files:", files);

            const response = await ApprovalService.createRequest(payload);

            console.log("Tạo yêu cầu thành công:", response);

            showToast(
                "success",
                "Gửi yêu cầu thành công!",
                "Yêu cầu của bạn đã được gửi đến cấp có thẩm quyền phê duyệt.",
                4000
            );

            // Reset dữ liệu và đóng Dialog sau khi gửi thành công
            setFormData(createForm());
            setFiles([]);

            onSuccess?.(response);
            onClose?.();

        } catch (err) {
            const status = err?.response?.status;
            const responseData = err?.response?.data;
            const message = getErrorMessage(err);

            console.error("========== LỖI TẠO YÊU CẦU ==========");
            console.error("HTTP Status:", status);
            console.error("Response:", responseData);
            console.error("Message:", message);
            console.error("====================================");

            const lowerMessage = String(message).toLowerCase();

            // Nhận diện lỗi trùng ngày nghỉ
            const isConflict =
                status === 409 ||
                lowerMessage.includes("trùng") ||
                lowerMessage.includes("chồng lịch") ||
                lowerMessage.includes("chồng lấn") ||
                lowerMessage.includes("overlap");

            if (isConflict) {
                showConflictToast();
            } else {
                showToast(
                    "error",
                    "Không thể tạo yêu cầu!",
                    message,
                    5000
                );
            }

        } finally {
            setLoading(false);
        }
    };

    // Footer
    const footer = (
        <div className="approval-form-footer">
            <Button
                label="Hủy"
                icon="pi pi-times"
                severity="secondary"
                outlined
                onClick={handleClose}
                disabled={loading}
            />

            <Button
                label="Gửi yêu cầu"
                icon="pi pi-send"
                onClick={handleSubmit}
                loading={loading}
                disabled={loading}
            />
        </div>
    );

    return (
        <>
            {/* Toast popup */}
            <Toast
                ref={toastRef}
                position="top-right"
                baseZIndex={99999}
                className="approval-toast"
            />

            <Dialog
                visible={visible}
                onHide={handleClose}
                modal
                closable={!loading}
                footer={footer}
                className="approval-form-dialog"
                style={{
                    width: "760px",
                    maxWidth: "95vw"
                }}
                header={
                    <div className="approval-form-dialog-header">
                        <div className="approval-form-dialog-icon">
                            <i className="pi pi-file-edit" />
                        </div>

                        <div>
                            <div className="approval-form-dialog-title">
                                Tạo yêu cầu
                            </div>

                            <div className="approval-form-dialog-subtitle">
                                Gửi yêu cầu để cấp có thẩm quyền phê duyệt
                            </div>
                        </div>
                    </div>
                }
            >
                <div className="approval-form">

                    {/* THÔNG TIN CHUNG */}
                    <section className="approval-form-section">
                        <div className="approval-form-section-title">
                            <i className="pi pi-info-circle" />
                            Thông tin yêu cầu
                        </div>

                        <div className="approval-form-grid">
                            <div className="approval-form-field">
                                <label>Mã yêu cầu</label>

                                <InputText
                                    value={formData.requestCode}
                                    readOnly
                                    disabled={loading}
                                />

                                <small>Mã được hệ thống tự động tạo</small>
                            </div>

                            <div className="approval-form-field">
                                <label>
                                    Loại yêu cầu <span>*</span>
                                </label>

                                <Dropdown
                                    value={formData.requestType}
                                    options={REQUEST_TYPES}
                                    optionLabel="label"
                                    optionValue="value"
                                    onChange={e => change("requestType", e.value)}
                                    placeholder="Chọn loại yêu cầu"
                                    className="approval-form-dropdown"
                                    disabled={loading}
                                />
                            </div>
                        </div>

                        {selectedType && (
                            <div className="approval-form-type-info">
                                <i className={selectedType.icon} />

                                <span>
                                    Bạn đang tạo: <strong>{selectedType.label}</strong>
                                </span>
                            </div>
                        )}

                        <div className="approval-form-field">
                            <label>
                                Tiêu đề <span>*</span>
                            </label>

                            <InputText
                                value={formData.title}
                                onChange={e => change("title", e.target.value)}
                                placeholder="Nhập tiêu đề yêu cầu"
                                disabled={loading}
                            />
                        </div>
                    </section>

                    <Divider />

                    {/* THỜI GIAN NGHỈ */}
                    {isLeave && (
                        <section className="approval-form-section">
                            <div className="approval-form-section-title">
                                <i className="pi pi-calendar" />
                                Thời gian nghỉ
                            </div>

                            <div className="approval-form-grid">
                                <div className="approval-form-field">
                                    <label>Từ ngày <span>*</span></label>

                                    <InputText
                                        type="date"
                                        value={formData.startDate}
                                        onChange={e => change("startDate", e.target.value)}
                                        disabled={loading}
                                    />
                                </div>

                                <div className="approval-form-field">
                                    <label>Đến ngày <span>*</span></label>

                                    <InputText
                                        type="date"
                                        value={formData.endDate}
                                        min={formData.startDate || undefined}
                                        onChange={e => change("endDate", e.target.value)}
                                        disabled={loading}
                                    />
                                </div>
                            </div>

                            {totalDays > 0 && (
                                <div className="approval-form-days">
                                    <i className="pi pi-clock" />
                                    <span>Tổng thời gian theo lịch:</span>
                                    <strong>{totalDays} ngày</strong>
                                </div>
                            )}

                            <div className="approval-form-field">
                                <label>Lý do <span>*</span></label>

                                <InputTextarea
                                    value={formData.reason}
                                    onChange={e => change("reason", e.target.value)}
                                    rows={4}
                                    autoResize
                                    placeholder={
                                        formData.requestType === "OM"
                                            ? "Nhập lý do nghỉ ốm..."
                                            : formData.requestType === "NGHI_PHEP"
                                                ? "Nhập lý do nghỉ phép năm..."
                                                : "Nhập lý do nghỉ..."
                                    }
                                    disabled={loading}
                                />
                            </div>
                        </section>
                    )}

                    {/* MUA HÀNG */}
                    {formData.requestType === "MUA_HANG" && (
                        <section className="approval-form-section">
                            <div className="approval-form-section-title">
                                <i className="pi pi-shopping-cart" />
                                Thông tin mua hàng
                            </div>

                            <div className="approval-form-grid">
                                <div className="approval-form-field">
                                    <label>Tên mặt hàng <span>*</span></label>

                                    <InputText
                                        value={formData.itemName}
                                        onChange={e => change("itemName", e.target.value)}
                                        placeholder="Nhập tên sản phẩm cần mua"
                                        disabled={loading}
                                    />
                                </div>

                                <div className="approval-form-field">
                                    <label>Số lượng <span>*</span></label>

                                    <InputNumber
                                        value={formData.quantity}
                                        onValueChange={e => change("quantity", e.value)}
                                        min={1}
                                        placeholder="Nhập số lượng"
                                        disabled={loading}
                                        className="w-full"
                                    />
                                </div>
                            </div>

                            <div className="approval-form-field">
                                <label>Lý do / mục đích mua</label>

                                <InputTextarea
                                    value={formData.reason}
                                    onChange={e => change("reason", e.target.value)}
                                    rows={4}
                                    autoResize
                                    placeholder="Nhập lý do hoặc mục đích mua hàng..."
                                    disabled={loading}
                                />
                            </div>
                        </section>
                    )}

                    {/* THANH TOÁN */}
                    {formData.requestType === "THANH_TOAN" && (
                        <section className="approval-form-section">
                            <div className="approval-form-section-title">
                                <i className="pi pi-money-bill" />
                                Thông tin thanh toán
                            </div>

                            <div className="approval-form-field">
                                <label>Số tiền <span>*</span></label>

                                <InputNumber
                                    value={formData.amount}
                                    onValueChange={e => change("amount", e.value)}
                                    mode="currency"
                                    currency="VND"
                                    locale="vi-VN"
                                    disabled={loading}
                                />
                            </div>

                            <div className="approval-form-field">
                                <label>Nội dung thanh toán <span>*</span></label>

                                <InputTextarea
                                    value={formData.reason}
                                    onChange={e => change("reason", e.target.value)}
                                    rows={4}
                                    autoResize
                                    placeholder="Nhập nội dung cần thanh toán..."
                                    disabled={loading}
                                />
                            </div>
                        </section>
                    )}

                    {/* NỘI DUNG BỔ SUNG */}
                    <section className="approval-form-section">
                        <div className="approval-form-section-title">
                            <i className="pi pi-align-left" />
                            Nội dung bổ sung
                        </div>

                        <div className="approval-form-field">
                            <label>Nội dung</label>

                            <InputTextarea
                                value={formData.content}
                                onChange={e => change("content", e.target.value)}
                                rows={4}
                                autoResize
                                placeholder="Nhập thêm thông tin nếu cần..."
                                disabled={loading}
                            />
                        </div>
                    </section>

                    {/* FILE ĐÍNH KÈM */}
                    <section className="approval-form-section">
                        <div className="approval-form-section-title">
                            <i className="pi pi-paperclip" />
                            Tài liệu đính kèm
                        </div>

                        <FileUpload
                            mode="advanced"
                            multiple
                            customUpload
                            accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
                            maxFileSize={5000000}
                            onSelect={e => setFiles(e.files)}
                            onRemove={e =>
                                setFiles(prev =>
                                    prev.filter(file => file !== e.file)
                                )
                            }
                            chooseLabel="Chọn tệp"
                            uploadLabel="Tải lên"
                            cancelLabel="Hủy"
                            emptyTemplate={
                                <div className="approval-form-upload-empty">
                                    <i className="pi pi-cloud-upload" />
                                    <span>Kéo thả hoặc chọn ảnh / tài liệu</span>
                                    <small>Tối đa 5MB / tệp</small>
                                </div>
                            }
                        />

                        <small className="approval-form-upload-note">
                            Có thể đính kèm giấy xác nhận, hóa đơn,
                            hình ảnh hoặc tài liệu liên quan.
                        </small>
                    </section>

                </div>
            </Dialog>
        </>
    );
}