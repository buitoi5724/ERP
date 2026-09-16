package com.example.erp.service;

import com.example.erp.entity.RequestApprove;

import java.util.List;

public interface RequestApproveService {

    // Lấy tất cả yêu cầu
    List<RequestApprove> getAll();

    // Lấy yêu cầu theo ID
    RequestApprove getById(Long id);

    // Lấy yêu cầu theo mã
    RequestApprove getByRequestCode(String requestCode);

    // Lấy các yêu cầu của một nhân viên
    List<RequestApprove> getByEmployee(Long empId);

    // Lấy các yêu cầu theo trạng thái
    List<RequestApprove> getByStatus(String status);

    // Tạo yêu cầu
    RequestApprove create(RequestApprove requestApprove);

    // Cập nhật yêu cầu
    RequestApprove update(
            Long id,
            RequestApprove requestApprove
    );

    // Hủy yêu cầu
    RequestApprove cancel(Long id);
}