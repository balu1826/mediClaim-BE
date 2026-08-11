package com.mediclaim.mediclaim.controller;

import com.mediclaim.mediclaim.dto.claim.CreatePolicyRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyResponse;
import com.mediclaim.mediclaim.entity.Policy;
import com.mediclaim.mediclaim.service.PolicyService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

	private final PolicyService policyService;

	public PolicyController(PolicyService policyService) {

		this.policyService = policyService;
	}

	@PostMapping
	@PreAuthorize("hasRole('TENANT_ADMIN')")
	public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {

		PolicyResponse policyResponse = policyService.createPolicy(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(policyResponse);
	}
	@GetMapping
	@PreAuthorize("hasRole('TENANT_ADMIN')")
	public ResponseEntity<List<PolicyResponse>> GetPolicies() {
		List<PolicyResponse> policyResponse = policyService.getAllPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(policyResponse);
	}
	@GetMapping("/{code}")
	@PreAuthorize("hasRole('TENANT_ADMIN')")
	public ResponseEntity<PolicyResponse> GetPolicy(@PathVariable String code) {
		PolicyResponse policyResponse = policyService.getPolicy(code);
		return ResponseEntity.status(HttpStatus.OK).body(policyResponse);
	}
}