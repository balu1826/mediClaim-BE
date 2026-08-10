package com.mediclaim.mediclaim.controller;

import com.mediclaim.mediclaim.dto.tenant.CreateTenantRequest;
import com.mediclaim.mediclaim.dto.tenant.TenantResponse;
import com.mediclaim.mediclaim.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
    	
        TenantResponse response =tenantService.createTenant(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TenantResponse>> getAllTenants() {

        return ResponseEntity.ok(tenantService.getAllTenants());
    }
}