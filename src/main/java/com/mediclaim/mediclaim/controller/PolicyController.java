package com.mediclaim.mediclaim.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediclaim.mediclaim.dto.claim.EnrollPolicyRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyResponse;
import com.mediclaim.mediclaim.dto.claim.PolicyUpgradeRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyUpgradeResponse;
import com.mediclaim.mediclaim.service.PolicyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

	private final PolicyService policyService;

	public PolicyController(PolicyService policyService) {

		this.policyService = policyService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('TENANT_ADMIN','PATIENT')")
	public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody EnrollPolicyRequest request) {

		PolicyResponse policyResponse = policyService.enrollPolicy(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(policyResponse);
	}

	@GetMapping
	@PreAuthorize("hasRole('TENANT_ADMIN','PATIENT')")
	public ResponseEntity<List<PolicyResponse>> GetPolicies() {
		List<PolicyResponse> policyResponse = policyService.getAllPolicies();
		return ResponseEntity.status(HttpStatus.OK).body(policyResponse);
	}

	@GetMapping("/{code}")
	@PreAuthorize("hasRole('TENANT_ADMIN','PATIENT')")
	public ResponseEntity<PolicyResponse> GetPolicy(@PathVariable String code) {
		PolicyResponse policyResponse = policyService.getPolicyByNumber(code);
		return ResponseEntity.status(HttpStatus.OK).body(policyResponse);
	}

	@PostMapping("/{policyId}/upgrade")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<PolicyUpgradeResponse> upgradePolicy(@PathVariable UUID policyId,
			@Valid @RequestBody PolicyUpgradeRequest request) {

		PolicyUpgradeResponse response = policyService.upgradePolicy(policyId, request.getTargetPolicyTypeId());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{policyId}/renew")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<PolicyResponse> renewPolicy(@PathVariable UUID policyId) {

		PolicyResponse response = policyService.renewPolicy(policyId);

		return ResponseEntity.ok(response);
	}
}