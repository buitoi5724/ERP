package com.example.erp.service.impl;

import com.example.erp.entity.Department;
import com.example.erp.repository.DepartmentRepository;
import com.example.erp.service.DepartmentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getById(Long deptId) {
        return departmentRepository.findById(deptId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy phòng ban với ID: " + deptId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Department getByDeptCode(String deptCode) {
        return departmentRepository.findByDeptCode(deptCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy phòng ban với mã: " + deptCode
                        )
                );
    }

    @Override
    public Department create(Department department) {

        if (department.getDeptCode() == null ||
                department.getDeptCode().trim().isEmpty()) {

            throw new RuntimeException(
                    "Mã phòng ban không được để trống"
            );
        }

        if (department.getDeptName() == null ||
                department.getDeptName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Tên phòng ban không được để trống"
            );
        }

        if (departmentRepository.existsByDeptCode(
                department.getDeptCode())) {

            throw new RuntimeException(
                    "Mã phòng ban đã tồn tại: "
                            + department.getDeptCode()
            );
        }

        if (department.getActive() == null) {
            department.setActive(true);
        }

        return departmentRepository.save(department);
    }

    @Override
    public Department update(
            Long deptId,
            Department department) {

        Department existingDepartment = getById(deptId);

        if (department.getDeptName() != null &&
                !department.getDeptName().trim().isEmpty()) {

            existingDepartment.setDeptName(
                    department.getDeptName()
            );
        }

        if (department.getActive() != null) {
            existingDepartment.setActive(
                    department.getActive()
            );
        }

        return departmentRepository.save(existingDepartment);
    }

    @Override
    public void delete(Long deptId) {

        Department department = getById(deptId);

        departmentRepository.delete(department);
    }
}