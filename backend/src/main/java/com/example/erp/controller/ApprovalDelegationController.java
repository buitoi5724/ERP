package com.example.erp.controller;

import com.example.erp.entity.ApprovalDelegation;
import com.example.erp.service.ApprovalDelegationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/approval-delegations")
@CrossOrigin(origins = "http://localhost:3000")
public class ApprovalDelegationController {

    private final ApprovalDelegationService approvalDelegationService;

    public ApprovalDelegationController(
            ApprovalDelegationService approvalDelegationService) {

        this.approvalDelegationService =
                approvalDelegationService;
    }

    // GET /api/approval-delegations
    @GetMapping
    public ResponseEntity<List<ApprovalDelegation>> getAll() {

        return ResponseEntity.ok(
                approvalDelegationService.getAll()
        );
    }

    // GET /api/approval-delegations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalDelegation> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                approvalDelegationService.getById(id)
        );
    }

    // GET /api/approval-delegations/from/{empId}
    @GetMapping("/from/{empId}")
    public ResponseEntity<List<ApprovalDelegation>> getByFromEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                approvalDelegationService
                        .getByFromEmployee(empId)
        );
    }

    // GET /api/approval-delegations/active/{empId}
    @GetMapping("/active/{empId}")
    public ResponseEntity<List<ApprovalDelegation>> getActiveDelegations(
            @PathVariable Long empId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                approvalDelegationService
                        .getActiveDelegations(empId, date)
        );
    }

    // GET /api/approval-delegations/to/{empId}
    @GetMapping("/to/{empId}")
    public ResponseEntity<List<ApprovalDelegation>> getByToEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                approvalDelegationService
                        .getByToEmployee(empId)
        );
    }

    // POST /api/approval-delegations
    @PostMapping
    public ResponseEntity<ApprovalDelegation> create(
            @RequestBody ApprovalDelegation approvalDelegation) {

        return ResponseEntity.ok(
                approvalDelegationService
                        .create(approvalDelegation)
        );
    }

    // PUT /api/approval-delegations/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalDelegation> update(
            @PathVariable Long id,
            @RequestBody ApprovalDelegation approvalDelegation) {

        return ResponseEntity.ok(
                approvalDelegationService.update(
                        id,
                        approvalDelegation
                )
        );
    }

    // DELETE /api/approval-delegations/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        approvalDelegationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}