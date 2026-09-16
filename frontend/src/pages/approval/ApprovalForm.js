
import React, { useEffect, useMemo, useState } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { Dropdown } from "primereact/dropdown";
import { InputNumber } from "primereact/inputnumber";
import { Button } from "primereact/button";
import { FileUpload } from "primereact/fileupload";
import { Divider } from "primereact/divider";

import ApprovalService from "./approvalService";

export default function ApprovalForm({
    visible,
    onClose,
    onSuccess
}) {

    // =========================
    // FORM DATA
    // =========================

    const [formData, setFormData] = useState({
        requestCode: "",
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

    const [files, setFiles] = useState([]);

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");


    // =========================
    // REQUEST TYPES
    // =========================

    const requestTypes = [
        {
            label: "Đơn ốm",
            value: "OM",
            icon: "pi pi-heart"
        },
        {
            label: "Nghỉ phép",
            value: "NGHI_PHEP",
            icon: "pi pi-calendar"
        },
        {
            label: "Đề nghị mua hàng",
            value: "MUA_HANG",
            icon: "pi pi-shopping-cart"
        },
        {
            label: "Đề nghị thanh toán",
            value: "THANH_TOAN",
            icon: "pi pi-money-bill"
        },
        {
            label: "Đề nghị xuất kho",
            value: "XUAT_KHO",
            icon: "pi pi-box"
        },
        {
            label: "Yêu cầu khác",
            value: "KHAC",
            icon: "pi pi-file"
        }
    ];


    // =========================
    // GENERATE REQUEST CODE
    // =========================

    const generateRequestCode = () => {

        const now = new Date();

        const year = now.getFullYear();

        const random =
            Math.floor(1000 + Math.random() * 9000);

        return `REQ-${year}-${random}`;
    };


    // =========================
    // RESET FORM
    // =========================

    const resetForm = () => {

        setFormData({
            requestCode: generateRequestCode(),
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

        setFiles([]);
        setError("");
    };


    // =========================
    // INITIALIZE
    // =========================

    useEffect(() => {

        if (visible) {
            resetForm();
        }

    }, [visible]);


    // =========================
    // CHANGE
    // =========================

    const handleChange = (field, value) => {

        setFormData(prev => ({
            ...prev,
            [field]: value
        }));

        setError("");
    };


    // =========================
    // SELECTED TYPE
    // =========================

    const selectedType = useMemo(() => {

        return requestTypes.find(
            item => item.value === formData.requestType
        );

    }, [formData.requestType]);


    // =========================
    // CALCULATE DAYS
    // =========================

    const totalDays = useMemo(() => {

        if (
            !formData.startDate ||
            !formData.endDate
        ) {
            return 0;
        }

        const start =
            new Date(formData.startDate);

        const end =
            new Date(formData.endDate);

        if (end < start) {
            return 0;
        }

        const difference =
            end.getTime() - start.getTime();

        return Math.floor(
            difference / (1000 * 60 * 60 * 24)
        ) + 1;

    }, [
        formData.startDate,
        formData.endDate
    ]);


    // =========================
    // VALIDATION
    // =========================

    const validateForm = () => {

        if (!formData.requestType) {
            return "Vui lòng chọn loại yêu cầu.";
        }

        if (!formData.title.trim()) {
            return "Vui lòng nhập tiêu đề yêu cầu.";
        }


        // Đơn ốm / nghỉ phép

        if (
            formData.requestType === "OM" ||
            formData.requestType === "NGHI_PHEP"
        ) {

            if (!formData.startDate) {
                return "Vui lòng chọn ngày bắt đầu.";
            }

            if (!formData.endDate) {
                return "Vui lòng chọn ngày kết thúc.";
            }

            if (
                new Date(formData.endDate) <
                new Date(formData.startDate)
            ) {
                return "Ngày kết thúc không được trước ngày bắt đầu.";
            }

            if (!formData.reason.trim()) {
                return "Vui lòng nhập lý do.";
            }
        }


        // Mua hàng

        if (formData.requestType === "MUA_HANG") {

            if (!formData.itemName.trim()) {
                return "Vui lòng nhập tên mặt hàng.";
            }

            if (
                !formData.quantity ||
                formData.quantity <= 0
            ) {
                return "Số lượng phải lớn hơn 0.";
            }
        }


        // Thanh toán

        if (formData.requestType === "THANH_TOAN") {

            if (
                !formData.amount ||
                formData.amount <= 0
            ) {
                return "Vui lòng nhập số tiền cần thanh toán.";
            }

            if (!formData.reason.trim()) {
                return "Vui lòng nhập nội dung thanh toán.";
            }
        }


        return null;
    };


    // =========================
    // SUBMIT
    // =========================

    const handleSubmit = async () => {

        setError("");

        const validationError =
            validateForm();

        if (validationError) {
            setError(validationError);
            return;
        }


        try {

            setLoading(true);


            const payload = {
                requestCode:
                    formData.requestCode,

                requestType:
                    formData.requestType,

                title:
                    formData.title.trim(),

                content:
                    formData.content.trim(),

                startDate:
                    formData.startDate || null,

                endDate:
                    formData.endDate || null,

                itemName:
                    formData.itemName.trim() || null,

                quantity:
                    formData.quantity || null,

                amount:
                    formData.amount || null,

                reason:
                    formData.reason.trim() || null
            };


            console.log(
                "===== CREATE APPROVAL REQUEST ====="
            );

            console.log(payload);

            console.log(
                "Attached files:",
                files
            );


            const response =
                await ApprovalService.createRequest(
                    payload
                );


            if (onSuccess) {
                onSuccess(response);
            }

            handleClose();

        } catch (err) {

            console.error(
                "Lỗi tạo yêu cầu phê duyệt:",
                err
            );

            const message =
                err?.response?.data?.message ||
                "Không thể tạo yêu cầu phê duyệt.";

            setError(message);

        } finally {

            setLoading(false);

        }
    };


    // =========================
    // CLOSE
    // =========================

    const handleClose = () => {

        resetForm();

        if (onClose) {
            onClose();
        }
    };


    // =========================
    // FILE UPLOAD
    // =========================

    const handleFileSelect = (event) => {

        setFiles(event.files);

    };


    const handleFileRemove = (event) => {

        setFiles(prev =>
            prev.filter(
                file => file.name !== event.file.name
            )
        );

    };


    // =========================
    // FOOTER
    // =========================

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
            />

        </div>
    );


    // =========================
    // RENDER
    // =========================

    return (

        <Dialog
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
            visible={visible}
            onHide={handleClose}
            footer={footer}
            modal
            closable={!loading}
            style={{
                width: "760px",
                maxWidth: "95vw"
            }}
        >

            <div className="approval-form">


                {/* ERROR */}

                {error && (

                    <div className="approval-form-error">

                        <i className="pi pi-exclamation-circle" />

                        <span>
                            {error}
                        </span>

                    </div>

                )}


                {/* =========================
                    BASIC INFORMATION
                ========================= */}

                <div className="approval-form-section">

                    <div className="approval-form-section-title">
                        <i className="pi pi-info-circle" />
                        Thông tin yêu cầu
                    </div>


                    <div className="approval-form-grid">

                        {/* REQUEST CODE */}

                        <div className="approval-form-field">

                            <label>
                                Mã yêu cầu
                            </label>

                            <InputText
                                value={
                                    formData.requestCode
                                }
                                readOnly
                                disabled={loading}
                            />

                            <small>
                                Mã được hệ thống tự động tạo
                            </small>

                        </div>


                        {/* REQUEST TYPE */}

                        <div className="approval-form-field">

                            <label>
                                Loại yêu cầu
                                <span>*</span>
                            </label>

                            <Dropdown
                                value={
                                    formData.requestType
                                }
                                options={requestTypes}
                                optionLabel="label"
                                optionValue="value"
                                onChange={(e) =>
                                    handleChange(
                                        "requestType",
                                        e.value
                                    )
                                }
                                placeholder="Chọn loại yêu cầu"
                                className="approval-form-dropdown"
                                disabled={loading}
                            />

                        </div>

                    </div>


                    {/* SELECTED TYPE */}

                    {selectedType && (

                        <div className="approval-form-type-info">

                            <i
                                className={
                                    selectedType.icon
                                }
                            />

                            <span>
                                Bạn đang tạo:
                                {" "}
                                <strong>
                                    {selectedType.label}
                                </strong>
                            </span>

                        </div>

                    )}


                    {/* TITLE */}

                    <div className="approval-form-field">

                        <label>
                            Tiêu đề
                            <span>*</span>
                        </label>

                        <InputText
                            value={
                                formData.title
                            }
                            onChange={(e) =>
                                handleChange(
                                    "title",
                                    e.target.value
                                )
                            }
                            placeholder="Ví dụ: Xin nghỉ ốm 3 ngày"
                            disabled={loading}
                        />

                    </div>

                </div>


                <Divider />


                {/* =========================
                    SICK LEAVE / ANNUAL LEAVE
                ========================= */}

                {(
                    formData.requestType === "OM" ||
                    formData.requestType === "NGHI_PHEP"
                ) && (

                    <div className="approval-form-section">

                        <div className="approval-form-section-title">

                            <i className="pi pi-calendar" />

                            Thời gian nghỉ

                        </div>


                        <div className="approval-form-grid">

                            <div className="approval-form-field">

                                <label>
                                    Từ ngày
                                    <span>*</span>
                                </label>

                                <InputText
                                    type="date"
                                    value={
                                        formData.startDate
                                    }
                                    onChange={(e) =>
                                        handleChange(
                                            "startDate",
                                            e.target.value
                                        )
                                    }
                                    disabled={loading}
                                />

                            </div>


                            <div className="approval-form-field">

                                <label>
                                    Đến ngày
                                    <span>*</span>
                                </label>

                                <InputText
                                    type="date"
                                    value={
                                        formData.endDate
                                    }
                                    onChange={(e) =>
                                        handleChange(
                                            "endDate",
                                            e.target.value
                                        )
                                    }
                                    disabled={loading}
                                />

                            </div>

                        </div>


                        {totalDays > 0 && (

                            <div className="approval-form-days">

                                <i className="pi pi-clock" />

                                <span>
                                    Thời gian nghỉ:
                                </span>

                                <strong>
                                    {totalDays} ngày
                                </strong>

                            </div>

                        )}


                        <div className="approval-form-field">

                            <label>
                                Lý do
                                <span>*</span>
                            </label>

                            <InputTextarea
                                value={
                                    formData.reason
                                }
                                onChange={(e) =>
                                    handleChange(
                                        "reason",
                                        e.target.value
                                    )
                                }
                                placeholder={
                                    formData.requestType === "OM"
                                        ? "Nhập lý do nghỉ ốm..."
                                        : "Nhập lý do nghỉ phép..."
                                }
                                rows={4}
                                autoResize
                                disabled={loading}
                            />

                        </div>

                    </div>

                )}


                {/* =========================
                    PURCHASE
                ========================= */}

                {formData.requestType === "MUA_HANG" && (

                    <div className="approval-form-section">

                        <div className="approval-form-section-title">

                            <i className="pi pi-shopping-cart" />

                            Thông tin mua hàng

                        </div>


                        <div className="approval-form-grid">

                            <div className="approval-form-field">

                                <label>
                                    Tên mặt hàng
                                    <span>*</span>
                                </label>

                                <InputText
                                    value={
                                        formData.itemName
                                    }
                                    onChange={(e) =>
                                        handleChange(
                                            "itemName",
                                            e.target.value
                                        )
                                    }
                                    placeholder="Nhập tên sản phẩm cần mua"
                                    disabled={loading}
                                />

                            </div>


                            <div className="approval-form-field">

                                <label>
                                    Số lượng
                                    <span>*</span>
                                </label>

                                <InputNumber
                                    value={
                                        formData.quantity
                                    }
                                    onValueChange={(e) =>
                                        handleChange(
                                            "quantity",
                                            e.value
                                        )
                                    }
                                    min={1}
                                    placeholder="Nhập số lượng"
                                    disabled={loading}
                                    className="w-full"
                                />

                            </div>

                        </div>


                        <div className="approval-form-field">

                            <label>
                                Lý do / mục đích mua
                            </label>

                            <InputTextarea
                                value={
                                    formData.reason
                                }
                                onChange={(e) =>
                                    handleChange(
                                        "reason",
                                        e.target.value
                                    )
                                }
                                rows={4}
                                autoResize
                                placeholder="Nhập lý do hoặc mục đích mua hàng..."
                                disabled={loading}
                            />

                        </div>

                    </div>

                )}


                {/* =========================
                    PAYMENT
                ========================= */}

                {formData.requestType === "THANH_TOAN" && (

                    <div className="approval-form-section">

                        <div className="approval-form-section-title">

                            <i className="pi pi-money-bill" />

                            Thông tin thanh toán

                        </div>


                        <div className="approval-form-field">

                            <label>
                                Số tiền
                                <span>*</span>
                            </label>

                            <InputNumber
                                value={
                                    formData.amount
                                }
                                onValueChange={(e) =>
                                    handleChange(
                                        "amount",
                                        e.value
                                    )
                                }
                                mode="currency"
                                currency="VND"
                                locale="vi-VN"
                                disabled={loading}
                            />

                        </div>


                        <div className="approval-form-field">

                            <label>
                                Nội dung thanh toán
                                <span>*</span>
                            </label>

                            <InputTextarea
                                value={
                                    formData.reason
                                }
                                onChange={(e) =>
                                    handleChange(
                                        "reason",
                                        e.target.value
                                    )
                                }
                                rows={4}
                                autoResize
                                placeholder="Nhập nội dung cần thanh toán..."
                                disabled={loading}
                            />

                        </div>

                    </div>

                )}


                {/* =========================
                    GENERAL CONTENT
                ========================= */}

                <div className="approval-form-section">

                    <div className="approval-form-section-title">

                        <i className="pi pi-align-left" />

                        Nội dung bổ sung

                    </div>


                    <div className="approval-form-field">

                        <label>
                            Nội dung
                        </label>

                        <InputTextarea
                            value={
                                formData.content
                            }
                            onChange={(e) =>
                                handleChange(
                                    "content",
                                    e.target.value
                                )
                            }
                            rows={4}
                            autoResize
                            placeholder="Nhập thêm thông tin nếu cần..."
                            disabled={loading}
                        />

                    </div>

                </div>


                {/* =========================
                    ATTACHMENTS
                ========================= */}

                <div className="approval-form-section">

                    <div className="approval-form-section-title">

                        <i className="pi pi-paperclip" />

                        Tài liệu đính kèm

                    </div>


                    <FileUpload
                        mode="advanced"
                        multiple
                        accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
                        maxFileSize={5000000}
                        customUpload
                        onSelect={handleFileSelect}
                        onRemove={handleFileRemove}
                        chooseLabel="Chọn tệp"
                        uploadLabel="Tải lên"
                        cancelLabel="Hủy"
                        emptyTemplate={
                            <div className="approval-form-upload-empty">

                                <i className="pi pi-cloud-upload" />

                                <span>
                                    Kéo thả hoặc chọn ảnh / tài liệu
                                </span>

                                <small>
                                    Tối đa 5MB / tệp
                                </small>

                            </div>
                        }
                    />

                    <small className="approval-form-upload-note">
                        Có thể đính kèm giấy xác nhận, hóa đơn,
                        hình ảnh hoặc tài liệu liên quan.
                    </small>

                </div>

            </div>

        </Dialog>

    );
}

