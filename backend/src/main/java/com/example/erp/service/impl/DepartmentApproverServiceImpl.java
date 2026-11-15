package com.example.erp.service.impl;

import com.example.erp.entity.DepartmentApprover;
import com.example.erp.repository.DepartmentApproverRepository;
import com.example.erp.service.DepartmentApproverService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentApproverServiceImpl
        implements DepartmentApproverService {

    private final DepartmentApproverRepository departmentApproverRepository;

    public DepartmentApproverServiceImpl(
            DepartmentApproverRepository departmentApproverRepository) {

        this.departmentApproverRepository =
                departmentApproverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentApprover> getAll() {
        return departmentApproverRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentApprover getById(Long id) {

        return departmentApproverRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy cấu hình người duyệt với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentApprover> getByDepartment(Long deptId) {

        return departmentApproverRepository
                .findByDepartmentDeptIdAndActiveTrueOrderByApprovalOrderAsc(
                        deptId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentApprover> getByEmployee(Long empId) {

        return departmentApproverRepository
                .findByEmployeeEmpIdAndActiveTrue(empId);
    }

    @Override
    public DepartmentApprover create(
            DepartmentApprover departmentApprover) {

        if (departmentApprover.getDepartment() == null) {
            throw new RuntimeException(
                    "Phòng ban không được để trống"
            );
        }

        if (departmentApprover.getEmployee() == null) {
            throw new RuntimeException(
                    "Người duyệt không được để trống"
            );
        }

        if (departmentApprover.getApprovalOrder() == null ||
                departmentApprover.getApprovalOrder() <= 0) {

            throw new RuntimeException(
                    "Thứ tự duyệt phải lớn hơn 0"
            );
        }

        if (departmentApprover.getActive() == null) {
            departmentApprover.setActive(true);
        }

        return departmentApproverRepository.save(
                departmentApprover
        );
    }

    @Override
    public DepartmentApprover update(
            Long id,
            DepartmentApprover departmentApprover) {

        DepartmentApprover existing =
                getById(id);

        if (departmentApprover.getDepartment() != null) {
            existing.setDepartment(
                    departmentApprover.getDepartment()
            );
        }

        if (departmentApprover.getEmployee() != null) {
            existing.setEmployee(
                    departmentApprover.getEmployee()
            );
        }

        if (departmentApprover.getApprovalOrder() != null) {

            if (departmentApprover.getApprovalOrder() <= 0) {
                throw new RuntimeException(
                        "Thứ tự duyệt phải lớn hơn 0"
                );
            }

            existing.setApprovalOrder(
                    departmentApprover.getApprovalOrder()
            );
        }

        if (departmentApprover.getActive() != null) {
            existing.setActive(
                    departmentApprover.getActive()
            );
        }

        return departmentApproverRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        DepartmentApprover departmentApprover =
                getById(id);

        departmentApproverRepository.delete(
                departmentApprover
        );
    }
}