package com.example.erp.controller;

import com.example.erp.entity.CMCode;
import com.example.erp.service.CMCodeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cm-codes")
@CrossOrigin(origins = "http://localhost:3000")
public class CMCodeController {

    private final CMCodeService cmCodeService;

    public CMCodeController(
            CMCodeService cmCodeService) {

        this.cmCodeService = cmCodeService;
    }

    // GET /api/cm-codes
    @GetMapping
    public ResponseEntity<List<CMCode>> getAll() {

        return ResponseEntity.ok(
                cmCodeService.getAll()
        );
    }

    // GET /api/cm-codes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CMCode> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cmCodeService.getById(id)
        );
    }

    // GET /api/cm-codes/group/{codeGroup}
    @GetMapping("/group/{codeGroup}")
    public ResponseEntity<List<CMCode>> getByCodeGroup(
            @PathVariable String codeGroup) {

        return ResponseEntity.ok(
                cmCodeService.getByCodeGroup(codeGroup)
        );
    }

    // GET /api/cm-codes/group/{codeGroup}/code/{code}
    @GetMapping("/group/{codeGroup}/code/{code}")
    public ResponseEntity<CMCode> getByCodeGroupAndCode(
            @PathVariable String codeGroup,
            @PathVariable String code) {

        return ResponseEntity.ok(
                cmCodeService.getByCodeGroupAndCode(
                        codeGroup,
                        code
                )
        );
    }

    // POST /api/cm-codes
    @PostMapping
    public ResponseEntity<CMCode> create(
            @RequestBody CMCode cmCode) {

        return ResponseEntity.ok(
                cmCodeService.create(cmCode)
        );
    }

    // PUT /api/cm-codes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CMCode> update(
            @PathVariable Long id,
            @RequestBody CMCode cmCode) {

        return ResponseEntity.ok(
                cmCodeService.update(
                        id,
                        cmCode
                )
        );
    }

    // DELETE /api/cm-codes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        cmCodeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}