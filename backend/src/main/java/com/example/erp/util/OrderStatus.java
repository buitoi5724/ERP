package com.example.erp.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {

    /**
     * Đơn hàng vừa được tạo
     * - Chưa xuất kho
     * - Có thể huỷ
     */
    PENDING,

    /**
     * Đã xác nhận đơn
     * - Kho đã bị trừ / giữ
     * - Chuẩn bị giao hàng
     */
    CONFIRMED,

    /**
     * Đã xuất kho / đang vận chuyển
     * - Không cho huỷ
     */
    SHIPPED,

    /**
     * Giao hàng thành công
     * - Hoàn tất đơn
     */
    COMPLETED,

    /**
     * Đơn bị huỷ
     * - Hoàn kho (chỉ khi chưa SHIPPED)
     */
    CANCELLED;

    /**
     * ✅ Cho phép parse không phân biệt hoa thường
     * JSON: "pending", "PENDING", "Pending" đều OK
     */
    @JsonCreator
    public static OrderStatus from(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }

    /**
     * ✅ Trả ra JSON đúng format enum
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    /**
     * ✅ Rule nghiệp vụ: có cho huỷ hay không
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }

    /**
     * ✅ Rule nghiệp vụ: có cho sửa order hay không
     */
    public boolean canBeUpdated() {
        return this == PENDING;
    }

    /**
     * ✅ Rule nghiệp vụ: có cho chuyển sang SHIPPED hay không
     */
    public boolean canBeShipped() {
        return this == CONFIRMED;
    }
}
