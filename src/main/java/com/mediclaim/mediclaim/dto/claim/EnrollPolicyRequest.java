package com.mediclaim.mediclaim.dto.claim;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class EnrollPolicyRequest {

	@NotNull(message = "Policy ID is required")
	private UUID policyTypeId;

	public UUID getPolicyTypeId() {
		return policyTypeId;
	}

	public void setPolicyTypeId(UUID policyId) {
		this.policyTypeId = policyId;
	}
}