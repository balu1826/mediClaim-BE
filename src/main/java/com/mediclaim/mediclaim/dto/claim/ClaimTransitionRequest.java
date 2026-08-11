package com.mediclaim.mediclaim.dto.claim;

import com.mediclaim.mediclaim.entity.ClaimStatus;

import jakarta.validation.constraints.NotNull;

public class ClaimTransitionRequest {

	@NotNull(message = "Target status is required")
	private ClaimStatus status;

	public ClaimStatus getStatus() {
		return status;
	}

	public void setStatus(ClaimStatus status) {
		this.status = status;
	}
}