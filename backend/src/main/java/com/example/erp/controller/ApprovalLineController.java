package com.example.erp.controller;

import com.example.erp.dto.ApprovalLineResponse;
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


    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<ApprovalLine>> getAll() {

        return ResponseEntity.ok(
                approvalLineService.getAll()
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalLine> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalLineService.getById(id)
        );
    }


    // =========================================================
    // GET BY APPROVAL ID
    // =========================================================

    @GetMapping("/approval/{approvalId}")
    public ResponseEntity<List<ApprovalLineResponse>>
    getByApprovalId(
            @PathVariable Long approvalId) {

        return ResponseEntity.ok(
                approvalLineService.getByApprovalId(
                        approvalId
                )
        );
    }


    // =========================================================
    // GET BY APPROVAL ID + ORDER
    // =========================================================

    @GetMapping("/approval/{approvalId}/order/{approvalOrder}")
    public ResponseEntity<ApprovalLine>
    getByApprovalIdAndOrder(
            @PathVariable Long approvalId,
            @PathVariable Integer approvalOrder) {

        return ResponseEntity.ok(
                approvalLineService.getByApprovalIdAndOrder(
                        approvalId,
                        approvalOrder
                )
        );
    }


    // =========================================================
    // GET BY APPROVER
    // =========================================================

    @GetMapping("/approver/{empId}")
    public ResponseEntity<List<ApprovalLine>>
    getByApprover(
            @PathVariable Long empId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                approvalLineService.getByApprover(
                        empId,
                        status
                )
        );
    }


    // =========================================================
    // GET BY ACTION EMPLOYEE
    // =========================================================

    @GetMapping("/action-employee/{empId}")
    public ResponseEntity<List<ApprovalLine>>
    getByActionEmployee(
            @PathVariable Long empId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                approvalLineService.getByActionEmployee(
                        empId,
                        status
                )
        );
    }


    // =========================================================
    // MY APPROVAL HISTORY
    // =========================================================

    @GetMapping("/my-history")
    public ResponseEntity<List<ApprovalLineResponse>>
    getMyApprovalHistory() {

        return ResponseEntity.ok(
                approvalLineService.getMyApprovalHistory()
        );
    }


    // =========================================================
    // MY PENDING APPROVALS
    // =========================================================

    @GetMapping("/my-pending")
    public ResponseEntity<List<ApprovalLineResponse>>
    getMyPendingApprovals() {

        return ResponseEntity.ok(
                approvalLineService.getMyPendingApprovals()
        );
    }


    // =========================================================
    // CREATE
    // =========================================================

    @PostMapping
    public ResponseEntity<ApprovalLine> create(
            @RequestBody ApprovalLine approvalLine) {

        return ResponseEntity.ok(
                approvalLineService.create(
                        approvalLine
                )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

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


    // =========================================================
    // APPROVE
    // =========================================================

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApprovalLineResponse> approve(
            @PathVariable Long id,
            @RequestParam(required = false)
            String comment) {

        return ResponseEntity.ok(
                approvalLineService.approve(
                        id,
                        id,
                        comment
                )
        );
    }


    // =========================================================
    // REJECT
    // =========================================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApprovalLineResponse> reject(
            @PathVariable Long id,
            @RequestParam(required = false)
            String comment) {

        return ResponseEntity.ok(
                approvalLineService.reject(
                        id,
                        id,
                        comment
                )
        );
    }
}