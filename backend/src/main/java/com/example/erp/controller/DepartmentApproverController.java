package com.example.erp.controller;

import com.example.erp.entity.DepartmentApprover;
import com.example.erp.service.DepartmentApproverService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department-approvers")
@CrossOrigin(origins = "http://localhost:3000")
public class DepartmentApproverController {

    private final DepartmentApproverService departmentApproverService;

    public DepartmentApproverController(
            DepartmentApproverService departmentApproverService) {

        this.departmentApproverService =
                departmentApproverService;
    }

    // GET /api/department-approvers
    @GetMapping
    public ResponseEntity<List<DepartmentApprover>> getAll() {

        return ResponseEntity.ok(
                departmentApproverService.getAll()
        );
    }

    // GET /api/department-approvers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentApprover> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentApproverService.getById(id)
        );
    }

    // GET /api/department-approvers/department/{deptId}
    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<DepartmentApprover>> getByDepartment(
            @PathVariable Long deptId) {

        return ResponseEntity.ok(
                departmentApproverService
                        .getByDepartment(deptId)
        );
    }

    // GET /api/department-approvers/employee/{empId}
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<DepartmentApprover>> getByEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                departmentApproverService
                        .getByEmployee(empId)
        );
    }

    // POST /api/department-approvers
    @PostMapping
    public ResponseEntity<DepartmentApprover> create(
            @RequestBody DepartmentApprover departmentApprover) {

        return ResponseEntity.ok(
                departmentApproverService
                        .create(departmentApprover)
        );
    }

    // PUT /api/department-approvers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentApprover> update(
            @PathVariable Long id,
            @RequestBody DepartmentApprover departmentApprover) {

        return ResponseEntity.ok(
                departmentApproverService.update(
                        id,
                        departmentApprover
                )
        );
    }

    // DELETE /api/department-approvers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        departmentApproverService.delete(id);

        return ResponseEntity.noContent().build();
    }
}