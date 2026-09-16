package com.example.erp.service.impl;

import com.example.erp.entity.Employee;
import com.example.erp.repository.EmployeeRepository;
import com.example.erp.service.EmployeeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Long empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với ID: " + empId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getByUserId(Long userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với User ID: " + userId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getByEmpCode(String empCode) {
        return employeeRepository.findByEmpCode(empCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy nhân viên với mã: " + empCode
                        )
                );
    }

    @Override
    public Employee create(Employee employee) {

        if (employee.getUser() == null) {
            throw new RuntimeException("User không được để trống");
        }

        if (employee.getEmpCode() == null ||
                employee.getEmpCode().trim().isEmpty()) {
            throw new RuntimeException("Mã nhân viên không được để trống");
        }

        if (employee.getEmpName() == null ||
                employee.getEmpName().trim().isEmpty()) {
            throw new RuntimeException("Tên nhân viên không được để trống");
        }

        if (employeeRepository.findByEmpCode(employee.getEmpCode()).isPresent()) {
            throw new RuntimeException(
                    "Mã nhân viên đã tồn tại: " + employee.getEmpCode()
            );
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long empId, Employee employee) {

        Employee existingEmployee = getById(empId);

        if (employee.getEmpName() != null &&
                !employee.getEmpName().trim().isEmpty()) {

            existingEmployee.setEmpName(employee.getEmpName());
        }

        existingEmployee.setEmail(employee.getEmail());

        if (employee.getActive() != null) {
            existingEmployee.setActive(employee.getActive());
        }

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void delete(Long empId) {

        Employee employee = getById(empId);

        employeeRepository.delete(employee);
    }
}