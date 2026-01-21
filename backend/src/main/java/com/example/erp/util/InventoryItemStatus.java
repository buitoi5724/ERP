package com.example.erp.util;

public enum  InventoryItemStatus {


	    AVAILABLE,     // còn dùng được
	    RESERVED,      // đã giữ cho đơn hàng
	    SOLD,          // đã xuất bán
	    EXPIRED,       // hết hạn
	    DAMAGED,       // hư hỏng
	    RETURNED,      // trả về NCC
	    DISABLED       // vô hiệu
	}