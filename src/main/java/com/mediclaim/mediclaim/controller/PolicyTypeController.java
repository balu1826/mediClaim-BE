package com.mediclaim.mediclaim.controller;

import com.mediclaim.mediclaim.dto.claim.CreatePolicyTypeRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyTypeResponse;
import com.mediclaim.mediclaim.service.PolicyTypeService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policy-types")
public class PolicyTypeController {

	private final PolicyTypeService policyTypeService;

	public PolicyTypeController(PolicyTypeService policyTypeService) {

		this.policyTypeService = policyTypeService;
	}

	@PostMapping
	@PreAuthorize("hasRole('TENANT_ADMIN')")
	public ResponseEntity<PolicyTypeResponse> createPolicyType(@Valid @RequestBody CreatePolicyTypeRequest request) {

		PolicyTypeResponse response = policyTypeService.createPolicyType(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('TENANT_ADMIN','PATIENT')")
	public ResponseEntity<List<PolicyTypeResponse>> getAvailablePolicyTypes() {

		return ResponseEntity.ok(policyTypeService.getAvailablePolicyTypes());
	}
}