package com.example.erp.controller;

import com.example.erp.entity.EmployeeDepartment;
import com.example.erp.service.EmployeeDepartmentService;

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

    // GET /api/employee-departments
    @GetMapping
    public ResponseEntity<List<EmployeeDepartment>> getAll() {

        return ResponseEntity.ok(
                employeeDepartmentService.getAll()
        );
    }

    // GET /api/employee-departments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDepartment> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeDepartmentService.getById(id)
        );
    }

    // GET /api/employee-departments/employee/{empId}
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<EmployeeDepartment>> getByEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                employeeDepartmentService.getByEmployee(empId)
        );
    }

    // GET /api/employee-departments/department/{deptId}
    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<EmployeeDepartment>> getByDepartment(
            @PathVariable Long deptId) {

        return ResponseEntity.ok(
                employeeDepartmentService.getByDepartment(deptId)
        );
    }

    // POST /api/employee-departments
    @PostMapping
    public ResponseEntity<EmployeeDepartment> create(
            @RequestBody EmployeeDepartment employeeDepartment) {

        return ResponseEntity.ok(
                employeeDepartmentService.create(
                        employeeDepartment
                )
        );
    }

    // PUT /api/employee-departments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDepartment> update(
            @PathVariable Long id,
            @RequestBody EmployeeDepartment employeeDepartment) {

        return ResponseEntity.ok(
                employeeDepartmentService.update(
                        id,
                        employeeDepartment
                )
        );
    }

    // DELETE /api/employee-departments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        employeeDepartmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}