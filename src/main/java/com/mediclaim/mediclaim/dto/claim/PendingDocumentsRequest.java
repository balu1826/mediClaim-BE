package com.mediclaim.mediclaim.dto.claim;

import jakarta.validation.constraints.NotBlank;

public class PendingDocumentsRequest {

	@NotBlank(message = "Document requirement is required")
	private String reason;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}