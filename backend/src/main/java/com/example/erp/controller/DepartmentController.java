package com.example.erp.controller;

import com.example.erp.entity.Department;
import com.example.erp.service.DepartmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:3000")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService = departmentService;
    }

    // GET /api/departments
    @GetMapping
    public ResponseEntity<List<Department>> getAll() {

        return ResponseEntity.ok(
                departmentService.getAll()
        );
    }

    // GET /api/departments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Department> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getById(id)
        );
    }

    // GET /api/departments/code/{deptCode}
    @GetMapping("/code/{deptCode}")
    public ResponseEntity<Department> getByDeptCode(
            @PathVariable String deptCode) {

        return ResponseEntity.ok(
                departmentService.getByDeptCode(deptCode)
        );
    }

    // POST /api/departments
    @PostMapping
    public ResponseEntity<Department> create(
            @RequestBody Department department) {

        return ResponseEntity.ok(
                departmentService.create(department)
        );
    }

    // PUT /api/departments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Department> update(
            @PathVariable Long id,
            @RequestBody Department department) {

        return ResponseEntity.ok(
                departmentService.update(
                        id,
                        department
                )
        );
    }

    // DELETE /api/departments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        departmentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}