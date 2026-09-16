package com.example.erp.controller;

import com.example.erp.entity.Approval;
import com.example.erp.service.ApprovalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "http://localhost:3000")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(
            ApprovalService approvalService) {

        this.approvalService = approvalService;
    }

    // GET /api/approvals
    @GetMapping
    public ResponseEntity<List<Approval>> getAll() {

        return ResponseEntity.ok(
                approvalService.getAll()
        );
    }

    // GET /api/approvals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Approval> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalService.getById(id)
        );
    }

    // GET /api/approvals/request/{requestId}
    @GetMapping("/request/{requestId}")
    public ResponseEntity<Approval> getByRequestId(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                approvalService.getByRequestId(requestId)
        );
    }

    // POST /api/approvals
    @PostMapping
    public ResponseEntity<Approval> create(
            @RequestBody Approval approval) {

        return ResponseEntity.ok(
                approvalService.create(approval)
        );
    }

    // PUT /api/approvals/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Approval> update(
            @PathVariable Long id,
            @RequestBody Approval approval) {

        return ResponseEntity.ok(
                approvalService.update(
                        id,
                        approval
                )
        );
    }
}