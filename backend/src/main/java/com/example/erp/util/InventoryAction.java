package com.example.erp.util;

public enum InventoryAction {

    IMPORT,        // Nhập kho từ nhà cung cấp
    EXPORT,        // Xuất kho bán hàng
    RETURN,        // Khách trả hàng
    ADJUST,        // Điều chỉnh tồn kho (kiểm kê)
    TRANSFER,      // Chuyển kho (nếu sau này có nhiều kho)
    CANCEL         // Hủy giao dịch / hoàn tác
, RELEASE, RESERVE
}