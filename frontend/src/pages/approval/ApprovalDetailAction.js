import React, { useState } from "react";

import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { InputTextarea } from "primereact/inputtextarea";

import ApprovalService from "./approvalService";

export default function ApprovalDetailAction({
    approvalLine,
    onSuccess
}) {
    const [dialogVisible, setDialogVisible] = useState(false);
    const [actionType, setActionType] = useState("");
    const [comment, setComment] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    /*
     * Chỉ ApprovalLine đang PENDING mới được xử lý.
     */
    const canProcess =
        approvalLine &&
        approvalLine.status === "PENDING";

    const openDialog = (type) => {
        if (!canProcess) {
            return;
        }

        setActionType(type);
        setComment("");
        setError("");
        setDialogVisible(true);
    };

    const closeDialog = () => {
        if (loading) {
            return;
        }

        setDialogVisible(false);
        setActionType("");
        setComment("");
        setError("");
    };

    const handleSubmit = async () => {
        if (!approvalLine?.id) {
            setError("Không tìm thấy bước phê duyệt.");
            return;
        }

        try {
            setLoading(true);
            setError("");

            let response;

            if (actionType === "APPROVE") {
                response = await ApprovalService.approve(
                    approvalLine.id,
                    comment.trim()
                );
            }

            if (actionType === "REJECT") {
                response = await ApprovalService.reject(
                    approvalLine.id,
                    comment.trim()
                );
            }

            setDialogVisible(false);
            setActionType("");
            setComment("");

            /*
             * Báo cho ApprovalDetailPage biết
             * hành động đã thực hiện thành công.
             */
            onSuccess?.(response);

        } catch (err) {
            console.error(
                "Lỗi khi xử lý phê duyệt:",
                err
            );

            setError(
                err?.response?.data?.message ||
                "Không thể xử lý yêu cầu. Vui lòng thử lại."
            );
        } finally {
            setLoading(false);
        }
    };

    if (!canProcess) {
        return null;
    }

    const isApprove = actionType === "APPROVE";

    return (
        <>
            <div className="approval-detail-action">

                <Button
                    label="Từ chối"
                    icon="pi pi-times"
                    severity="danger"
                    outlined
                    onClick={() => openDialog("REJECT")}
                    disabled={loading}
                />

                <Button
                    label="Duyệt"
                    icon="pi pi-check"
                    severity="success"
                    onClick={() => openDialog("APPROVE")}
                    disabled={loading}
                />

            </div>

            <Dialog
                visible={dialogVisible}
                onHide={closeDialog}
                modal
                closable={!loading}
                style={{
                    width: "500px",
                    maxWidth: "95vw"
                }}
                header={
                    isApprove
                        ? "Xác nhận phê duyệt"
                        : "Từ chối yêu cầu"
                }
                footer={
                    <div className="approval-detail-action-footer">

                        <Button
                            label="Hủy"
                            icon="pi pi-times"
                            severity="secondary"
                            outlined
                            onClick={closeDialog}
                            disabled={loading}
                        />

                        <Button
                            label={
                                isApprove
                                    ? "Xác nhận duyệt"
                                    : "Xác nhận từ chối"
                            }
                            icon={
                                isApprove
                                    ? "pi pi-check"
                                    : "pi pi-times"
                            }
                            severity={
                                isApprove
                                    ? "success"
                                    : "danger"
                            }
                            onClick={handleSubmit}
                            loading={loading}
                        />

                    </div>
                }
            >
                <div className="approval-detail-action-dialog">

                    <div className="approval-detail-action-message">

                        <i
                            className={
                                isApprove
                                    ? "pi pi-check-circle"
                                    : "pi pi-exclamation-triangle"
                            }
                        />

                        <span>
                            {isApprove
                                ? "Bạn có chắc chắn muốn phê duyệt yêu cầu này?"
                                : "Bạn có chắc chắn muốn từ chối yêu cầu này?"}
                        </span>

                    </div>

                    <div className="approval-detail-action-field">

                        <label>
                            Nhận xét
                        </label>

                        <InputTextarea
                            value={comment}
                            onChange={(e) =>
                                setComment(e.target.value)
                            }
                            rows={4}
                            autoResize
                            placeholder={
                                isApprove
                                    ? "Nhập nhận xét nếu cần..."
                                    : "Nhập lý do từ chối..."
                            }
                            disabled={loading}
                        />

                    </div>

                    {error && (
                        <div className="approval-form-error">
                            <i className="pi pi-exclamation-circle" />
                            <span>{error}</span>
                        </div>
                    )}

                </div>
            </Dialog>
        </>
    );
}