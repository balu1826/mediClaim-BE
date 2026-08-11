package com.mediclaim.mediclaim.dto.claim;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class PolicyUpgradeRequest {

	@NotNull(message = "Target policy type is required")
	private UUID targetPolicyTypeId;

	public UUID getTargetPolicyTypeId() {
		return targetPolicyTypeId;
	}

	public void setTargetPolicyTypeId(UUID targetPolicyTypeId) {
		this.targetPolicyTypeId = targetPolicyTypeId;
	}
}