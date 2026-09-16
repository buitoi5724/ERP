package com.example.erp.controller;

import com.example.erp.entity.RequestTemplate;
import com.example.erp.service.RequestTemplateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-templates")
@CrossOrigin(origins = "http://localhost:3000")
public class RequestTemplateController {

    private final RequestTemplateService requestTemplateService;

    public RequestTemplateController(
            RequestTemplateService requestTemplateService) {

        this.requestTemplateService = requestTemplateService;
    }

    // GET /api/request-templates
    @GetMapping
    public ResponseEntity<List<RequestTemplate>> getAll() {

        return ResponseEntity.ok(
                requestTemplateService.getAll()
        );
    }

    // GET /api/request-templates/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RequestTemplate> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestTemplateService.getById(id)
        );
    }

    // GET /api/request-templates/code/{templateCode}
    @GetMapping("/code/{templateCode}")
    public ResponseEntity<RequestTemplate> getByTemplateCode(
            @PathVariable String templateCode) {

        return ResponseEntity.ok(
                requestTemplateService
                        .getByTemplateCode(templateCode)
        );
    }

    // GET /api/request-templates/type/{requestType}
    @GetMapping("/type/{requestType}")
    public ResponseEntity<List<RequestTemplate>> getByRequestType(
            @PathVariable String requestType) {

        return ResponseEntity.ok(
                requestTemplateService
                        .getByRequestType(requestType)
        );
    }

    // GET /api/request-templates/active
    @GetMapping("/active")
    public ResponseEntity<List<RequestTemplate>> getActive() {

        return ResponseEntity.ok(
                requestTemplateService.getActive()
        );
    }

    // POST /api/request-templates
    @PostMapping
    public ResponseEntity<RequestTemplate> create(
            @RequestBody RequestTemplate requestTemplate) {

        return ResponseEntity.ok(
                requestTemplateService
                        .create(requestTemplate)
        );
    }

    // PUT /api/request-templates/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RequestTemplate> update(
            @PathVariable Long id,
            @RequestBody RequestTemplate requestTemplate) {

        return ResponseEntity.ok(
                requestTemplateService
                        .update(id, requestTemplate)
        );
    }

    // DELETE /api/request-templates/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        requestTemplateService.delete(id);

        return ResponseEntity.noContent().build();
    }
}