package com.example.erp.service.impl;

import com.example.erp.entity.Department;
import com.example.erp.entity.Employee;
import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.repository.DepartmentRepository;
import com.example.erp.repository.EmployeeDepartmentRepository;
import com.example.erp.repository.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeDepartmentServiceImpl(
            EmployeeDepartmentRepository employeeDepartmentRepository,
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {

        this.employeeDepartmentRepository =
                employeeDepartmentRepository;

        this.employeeRepository =
                employeeRepository;

        this.departmentRepository =
                departmentRepository;
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
                                "Không tìm thấy phân công phòng ban với ID: "
                                        + id
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

        if (employeeDepartment.getEmployee() == null
                || employeeDepartment.getEmployee().getEmpId() == null) {

            throw new RuntimeException(
                    "Nhân viên không được để trống"
            );
        }

        if (employeeDepartment.getDepartment() == null
                || employeeDepartment.getDepartment().getDeptId() == null) {

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

        // Tìm Employee thật trong database
        Long empId =
                employeeDepartment
                        .getEmployee()
                        .getEmpId();

        Employee employee =
                employeeRepository.findById(empId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy nhân viên với ID: "
                                                + empId
                                )
                        );

        // Tìm Department thật trong database
        Long deptId =
                employeeDepartment
                        .getDepartment()
                        .getDeptId();

        Department department =
                departmentRepository.findById(deptId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy phòng ban với ID: "
                                                + deptId
                                )
                        );

        LocalDate newFromDate =
                employeeDepartment.getFromDate();

        // Kiểm tra phòng ban hiện tại của nhân viên
        employeeDepartmentRepository
                .findByEmployeeEmpIdAndFromDateLessThanEqualAndToDateGreaterThanEqual(
                        empId,
                        newFromDate,
                        newFromDate
                )
                .ifPresent(currentDepartment -> {

                    // Nếu đang gán đúng phòng ban hiện tại
                    // thì không cần tạo thêm bản ghi trùng
                    if (currentDepartment
                            .getDepartment()
                            .getDeptId()
                            .equals(deptId)) {

                        throw new RuntimeException(
                                "Nhân viên đã thuộc phòng ban này"
                        );
                    }

                    // Kết thúc phòng ban cũ
                    LocalDate endDate =
                            newFromDate.minusDays(1);

                    currentDepartment.setToDate(endDate);

                    employeeDepartmentRepository.save(
                            currentDepartment
                    );
                });

        // Gán Employee thật
        employeeDepartment.setEmployee(employee);

        // Gán Department thật
        employeeDepartment.setDepartment(department);

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

        if (employeeDepartment.getEmployee() != null
                && employeeDepartment.getEmployee().getEmpId() != null) {

            Long empId =
                    employeeDepartment
                            .getEmployee()
                            .getEmpId();

            Employee employee =
                    employeeRepository.findById(empId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Không tìm thấy nhân viên với ID: "
                                                    + empId
                                    )
                            );

            existing.setEmployee(employee);
        }

        if (employeeDepartment.getDepartment() != null
                && employeeDepartment.getDepartment().getDeptId() != null) {

            Long deptId =
                    employeeDepartment
                            .getDepartment()
                            .getDeptId();

            Department department =
                    departmentRepository.findById(deptId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Không tìm thấy phòng ban với ID: "
                                                    + deptId
                                    )
                            );

            existing.setDepartment(department);
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

        return employeeDepartmentRepository.save(
                existing
        );
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