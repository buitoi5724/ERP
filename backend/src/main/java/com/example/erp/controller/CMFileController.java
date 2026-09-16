package com.example.erp.controller;

import com.example.erp.entity.CMFile;
import com.example.erp.service.CMFileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cm-files")
@CrossOrigin(origins = "http://localhost:3000")
public class CMFileController {

    private final CMFileService cmFileService;

    public CMFileController(
            CMFileService cmFileService) {

        this.cmFileService = cmFileService;
    }

    // GET /api/cm-files
    @GetMapping
    public ResponseEntity<List<CMFile>> getAll() {

        return ResponseEntity.ok(
                cmFileService.getAll()
        );
    }

    // GET /api/cm-files/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CMFile> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cmFileService.getById(id)
        );
    }

    // GET /api/cm-files/request/{requestId}
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<CMFile>> getByRequestId(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                cmFileService.getByRequestId(requestId)
        );
    }

    // GET /api/cm-files/request/{requestId}/latest
    @GetMapping("/request/{requestId}/latest")
    public ResponseEntity<List<CMFile>> getLatestFiles(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                cmFileService
                        .getByRequestIdOrderByUploadedAtDesc(
                                requestId
                        )
        );
    }

    // POST /api/cm-files
    @PostMapping
    public ResponseEntity<CMFile> create(
            @RequestBody CMFile cmFile) {

        return ResponseEntity.ok(
                cmFileService.create(cmFile)
        );
    }

    // PUT /api/cm-files/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CMFile> update(
            @PathVariable Long id,
            @RequestBody CMFile cmFile) {

        return ResponseEntity.ok(
                cmFileService.update(
                        id,
                        cmFile
                )
        );
    }

    // DELETE /api/cm-files/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        cmFileService.delete(id);

        return ResponseEntity.noContent().build();
    }
}