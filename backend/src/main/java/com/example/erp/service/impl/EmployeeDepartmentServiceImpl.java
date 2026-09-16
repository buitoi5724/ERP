package com.example.erp.service.impl;

import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.service.EmployeeDepartmentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EmployeeDepartmentServiceImpl
        implements EmployeeDepartmentService {

    private final EmployeeDepartmentRepository employeeDepartmentRepository;

    public EmployeeDepartmentServiceImpl(
            EmployeeDepartmentRepository employeeDepartmentRepository) {

        this.employeeDepartmentRepository =
                employeeDepartmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDepartment> getAll() {
        return employeeDepartmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDepartment getById(Long id) {

        return employeeDepartmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy phân công phòng ban với ID: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDepartment> getByEmployee(Long empId) {

        return employeeDepartmentRepository
                .findByEmployeeEmpId(empId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDepartment> getByDepartment(Long deptId) {

        return employeeDepartmentRepository
                .findByDepartmentDeptId(deptId);
    }

    @Override
    public EmployeeDepartment create(
            EmployeeDepartment employeeDepartment) {

        if (employeeDepartment.getEmployee() == null) {
            throw new RuntimeException(
                    "Nhân viên không được để trống"
            );
        }

        if (employeeDepartment.getDepartment() == null) {
            throw new RuntimeException(
                    "Phòng ban không được để trống"
            );
        }

        if (employeeDepartment.getFromDate() == null) {
            throw new RuntimeException(
                    "Ngày bắt đầu không được để trống"
            );
        }

        if (employeeDepartment.getToDate() == null) {
            throw new RuntimeException(
                    "Ngày kết thúc không được để trống"
            );
        }

        if (employeeDepartment.getFromDate()
                .isAfter(employeeDepartment.getToDate())) {

            throw new RuntimeException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc"
            );
        }

        return employeeDepartmentRepository.save(
                employeeDepartment
        );
    }

    @Override
    public EmployeeDepartment update(
            Long id,
            EmployeeDepartment employeeDepartment) {

        EmployeeDepartment existing =
                getById(id);

        if (employeeDepartment.getEmployee() != null) {
            existing.setEmployee(
                    employeeDepartment.getEmployee()
            );
        }

        if (employeeDepartment.getDepartment() != null) {
            existing.setDepartment(
                    employeeDepartment.getDepartment()
            );
        }

        if (employeeDepartment.getFromDate() != null) {
            existing.setFromDate(
                    employeeDepartment.getFromDate()
            );
        }

        if (employeeDepartment.getToDate() != null) {
            existing.setToDate(
                    employeeDepartment.getToDate()
            );
        }

        if (existing.getFromDate()
                .isAfter(existing.getToDate())) {

            throw new RuntimeException(
                    "Ngày bắt đầu không được lớn hơn ngày kết thúc"
            );
        }

        return employeeDepartmentRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        EmployeeDepartment employeeDepartment =
                getById(id);

        employeeDepartmentRepository.delete(
                employeeDepartment
        );
    }
}