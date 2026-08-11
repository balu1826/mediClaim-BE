package com.mediclaim.mediclaim.dto.claim;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class ApproveClaimRequest {

	@NotNull(message = "Approved amount is required")
	@DecimalMin(value = "0.01", message = "Approved amount must be greater than zero")
	private BigDecimal approvedAmount;

	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(BigDecimal approvedAmount) {
		this.approvedAmount = approvedAmount;
	}
}