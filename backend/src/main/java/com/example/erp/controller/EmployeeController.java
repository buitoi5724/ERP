package com.example.erp.controller;

import com.example.erp.entity.Employee;
import com.example.erp.service.EmployeeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    // GET /api/employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {

        return ResponseEntity.ok(
                employeeService.getAll()
        );
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeService.getById(id)
        );
    }

    // GET /api/employees/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<Employee> getByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                employeeService.getByUserId(userId)
        );
    }

    // GET /api/employees/code/{empCode}
    @GetMapping("/code/{empCode}")
    public ResponseEntity<Employee> getByEmpCode(
            @PathVariable String empCode) {

        return ResponseEntity.ok(
                employeeService.getByEmpCode(empCode)
        );
    }

    // POST /api/employees
    @PostMapping
    public ResponseEntity<Employee> create(
            @RequestBody Employee employee) {

        return ResponseEntity.ok(
                employeeService.create(employee)
        );
    }

    // PUT /api/employees/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(
            @PathVariable Long id,
            @RequestBody Employee employee) {

        return ResponseEntity.ok(
                employeeService.update(id, employee)
        );
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        employeeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}