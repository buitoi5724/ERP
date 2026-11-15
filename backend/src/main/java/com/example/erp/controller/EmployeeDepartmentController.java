package com.example.erp.controller;

import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.service.EmployeeDepartmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-departments")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeDepartmentController {

    private final EmployeeDepartmentService employeeDepartmentService;

    public EmployeeDepartmentController(
            EmployeeDepartmentService employeeDepartmentService) {

        this.employeeDepartmentService =
                employeeDepartmentService;
    }

    // =========================
    // Lấy tất cả phân công
    // =========================
    @GetMapping
    public ResponseEntity<List<EmployeeDepartment>> getAll() {

        return ResponseEntity.ok(
                employeeDepartmentService.getAll()
        );
    }

    // =========================
    // Lấy phân công theo ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDepartment> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeDepartmentService.getById(id)
        );
    }

    // =========================
    // Lấy phòng ban của nhân viên
    // =========================
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<EmployeeDepartment>> getByEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                employeeDepartmentService
                        .getByEmployee(empId)
        );
    }

    // =========================
    // Lấy nhân viên của phòng ban
    // =========================
    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<EmployeeDepartment>> getByDepartment(
            @PathVariable Long deptId) {

        return ResponseEntity.ok(
                employeeDepartmentService
                        .getByDepartment(deptId)
        );
    }

    // =========================
    // Tạo phân công phòng ban
    // =========================
    @PostMapping
    public ResponseEntity<EmployeeDepartment> create(
            @RequestBody EmployeeDepartment employeeDepartment) {

        EmployeeDepartment created =
                employeeDepartmentService
                        .create(employeeDepartment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // =========================
    // Cập nhật phân công
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDepartment> update(
            @PathVariable Long id,
            @RequestBody EmployeeDepartment employeeDepartment) {

        return ResponseEntity.ok(
                employeeDepartmentService
                        .update(id, employeeDepartment)
        );
    }

    // =========================
    // Xóa phân công
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        employeeDepartmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}