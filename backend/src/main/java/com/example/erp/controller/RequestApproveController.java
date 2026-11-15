
package com.example.erp.controller;

import com.example.erp.dto.ApprovalLineResponse;
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
        this.requestApproveService = requestApproveService;
    }

    // =========================================================
    // GET ALL
    // GET /api/request-approves
    // =========================================================

    @GetMapping
    public ResponseEntity<List<RequestApprove>> getAll() {
        return ResponseEntity.ok(
                requestApproveService.getAll()
        );
    }

    // =========================================================
    // GET MY REQUESTS
    // GET /api/request-approves/my
    // =========================================================

    @GetMapping("/my")
    public ResponseEntity<List<RequestApprove>> getMyRequests() {
        return ResponseEntity.ok(
                requestApproveService.getMyRequests()
        );
    }

    // =========================================================
    // GET BY ID
    // GET /api/request-approves/{id}
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<RequestApprove> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                requestApproveService.getById(id)
        );
    }

    // =========================================================
    // GET PENDING APPROVALS
    // GET /api/request-approves/pending-approvals
    // =========================================================

    @GetMapping("/pending-approvals")
    public ResponseEntity<List<ApprovalLineResponse>> getPendingApprovals() {
        return ResponseEntity.ok(
                requestApproveService.getPendingApprovals()
        );
    }

    // =========================================================
    // GET BY REQUEST CODE
    // GET /api/request-approves/code/{requestCode}
    // =========================================================

    @GetMapping("/code/{requestCode}")
    public ResponseEntity<RequestApprove> getByRequestCode(
            @PathVariable String requestCode) {
        return ResponseEntity.ok(
                requestApproveService.getByRequestCode(requestCode)
        );
    }

    // =========================================================
    // GET BY EMPLOYEE
    // GET /api/request-approves/employee/{empId}
    // =========================================================

    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<RequestApprove>> getByEmployee(
            @PathVariable Long empId) {
        return ResponseEntity.ok(
                requestApproveService.getByEmployee(empId)
        );
    }

    // =========================================================
    // GET BY STATUS
    // GET /api/request-approves/status/{status}
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequestApprove>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(
                requestApproveService.getByStatus(status)
        );
    }

    // =========================================================
    // CREATE REQUEST
    // POST /api/request-approves
    // =========================================================

    @PostMapping
    public ResponseEntity<RequestApprove> create(
            @RequestBody RequestApprove requestApprove) {
        return ResponseEntity.ok(
                requestApproveService.create(requestApprove)
        );
    }

    // =========================================================
    // UPDATE
    // PUT /api/request-approves/{id}
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<RequestApprove> update(
            @PathVariable Long id,
            @RequestBody RequestApprove requestApprove) {
        return ResponseEntity.ok(
                requestApproveService.update(id, requestApprove)
        );
    }

    // =========================================================
    // CANCEL
    // PUT /api/request-approves/{id}/cancel
    // =========================================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RequestApprove> cancel(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                requestApproveService.cancel(id)
        );
    }
}
