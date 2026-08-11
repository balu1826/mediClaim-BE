package com.mediclaim.mediclaim.dto.claim;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpgradePolicyRequest {

	@NotNull(message = "New policy type ID is required")
	private UUID newPolicyTypeId;

	public UUID getNewPolicyTypeId() {
		return newPolicyTypeId;
	}

	public void setNewPolicyTypeId(UUID newPolicyTypeId) {
		this.newPolicyTypeId = newPolicyTypeId;
	}
}