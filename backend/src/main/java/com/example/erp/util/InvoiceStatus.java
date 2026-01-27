package com.example.erp.util;

public enum InvoiceStatus {
    DRAFT,      // tạo nháp
    DOING,      // chờ thanh toán
    PARTIAL,    // thanh toán một phần
    DONE,       // đã thanh toán
    CANCEL      // hủy
, PAID
}