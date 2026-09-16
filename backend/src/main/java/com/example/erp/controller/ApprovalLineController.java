package com.example.erp.controller;

import com.example.erp.entity.ApprovalLine;
import com.example.erp.service.ApprovalLineService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-lines")
@CrossOrigin(origins = "http://localhost:3000")
public class ApprovalLineController {

    private final ApprovalLineService approvalLineService;

    public ApprovalLineController(
            ApprovalLineService approvalLineService) {

        this.approvalLineService = approvalLineService;
    }

    // GET /api/approval-lines
    @GetMapping
    public ResponseEntity<List<ApprovalLine>> getAll() {

        return ResponseEntity.ok(
                approvalLineService.getAll()
        );
    }

    // GET /api/approval-lines/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalLine> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalLineService.getById(id)
        );
    }

    // GET /api/approval-lines/approval/{approvalId}
    @GetMapping("/approval/{approvalId}")
    public ResponseEntity<List<ApprovalLine>> getByApprovalId(
            @PathVariable Long approvalId) {

        return ResponseEntity.ok(
                approvalLineService
                        .getByApprovalId(approvalId)
        );
    }

    // GET /api/approval-lines/approval/{approvalId}/order/{approvalOrder}
    @GetMapping("/approval/{approvalId}/order/{approvalOrder}")
    public ResponseEntity<ApprovalLine> getByApprovalIdAndOrder(
            @PathVariable Long approvalId,
            @PathVariable Integer approvalOrder) {

        return ResponseEntity.ok(
                approvalLineService
                        .getByApprovalIdAndOrder(
                                approvalId,
                                approvalOrder
                        )
        );
    }

    // GET /api/approval-lines/approver/{empId}?status=PENDING
    @GetMapping("/approver/{empId}")
    public ResponseEntity<List<ApprovalLine>> getByApprover(
            @PathVariable Long empId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                approvalLineService
                        .getByApprover(
                                empId,
                                status
                        )
        );
    }

    // GET /api/approval-lines/action-employee/{empId}?status=APPROVED
    @GetMapping("/action-employee/{empId}")
    public ResponseEntity<List<ApprovalLine>> getByActionEmployee(
            @PathVariable Long empId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                approvalLineService
                        .getByActionEmployee(
                                empId,
                                status
                        )
        );
    }

    // POST /api/approval-lines
    @PostMapping
    public ResponseEntity<ApprovalLine> create(
            @RequestBody ApprovalLine approvalLine) {

        return ResponseEntity.ok(
                approvalLineService.create(
                        approvalLine
                )
        );
    }

    // PUT /api/approval-lines/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalLine> update(
            @PathVariable Long id,
            @RequestBody ApprovalLine approvalLine) {

        return ResponseEntity.ok(
                approvalLineService.update(
                        id,
                        approvalLine
                )
        );
    }

    // PUT /api/approval-lines/{id}/approve
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApprovalLine> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalLineService.approve(
                        id,id, comment
                )
        );
    }

    // PUT /api/approval-lines/{id}/reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApprovalLine> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String comment) {

        return ResponseEntity.ok(
                approvalLineService.reject(
                        id,
                        id, comment
                )
        );
    }
}