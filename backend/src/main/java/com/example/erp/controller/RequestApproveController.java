package com.example.erp.controller;

import com.example.erp.entity.RequestApprove;
import com.example.erp.service.RequestApproveService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-approves")
@CrossOrigin(origins = "http://localhost:3000")
public class RequestApproveController {

    private final RequestApproveService requestApproveService;

    public RequestApproveController(
            RequestApproveService requestApproveService) {

        this.requestApproveService =
                requestApproveService;
    }

    // GET /api/request-approves
    @GetMapping
    public ResponseEntity<List<RequestApprove>> getAll() {

        return ResponseEntity.ok(
                requestApproveService.getAll()
        );
    }

    // GET /api/request-approves/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RequestApprove> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestApproveService.getById(id)
        );
    }

    // GET /api/request-approves/code/{requestCode}
    @GetMapping("/code/{requestCode}")
    public ResponseEntity<RequestApprove> getByRequestCode(
            @PathVariable String requestCode) {

        return ResponseEntity.ok(
                requestApproveService
                        .getByRequestCode(requestCode)
        );
    }

    // GET /api/request-approves/employee/{empId}
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<RequestApprove>> getByEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                requestApproveService
                        .getByEmployee(empId)
        );
    }

    // GET /api/request-approves/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestApprove>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                requestApproveService
                        .getByStatus(status)
        );
    }

    // POST /api/request-approves
    @PostMapping
    public ResponseEntity<RequestApprove> create(
            @RequestBody RequestApprove requestApprove) {

        return ResponseEntity.ok(
                requestApproveService
                        .create(requestApprove)
        );
    }

    // PUT /api/request-approves/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RequestApprove> update(
            @PathVariable Long id,
            @RequestBody RequestApprove requestApprove) {

        return ResponseEntity.ok(
                requestApproveService.update(
                        id,
                        requestApprove
                )
        );
    }

    // PUT /api/request-approves/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<RequestApprove> cancel(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestApproveService.cancel(id)
        );
    }
}