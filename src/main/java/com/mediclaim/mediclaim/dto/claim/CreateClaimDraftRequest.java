package com.mediclaim.mediclaim.dto.claim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateClaimDraftRequest {

	@NotNull(message = "Policy ID is required")
	private UUID policyId;

	@NotNull(message = "Treatment date is required")
	private LocalDate treatmentDate;

	@NotBlank(message = "ICD-10 code is required")
	private String icd10Code;

	@NotNull(message = "Provider ID is required")
	private UUID providerId;

	@NotNull(message = "Claim amount is required")
	@DecimalMin(value = "0.01", message = "Claim amount must be greater than zero")
	private BigDecimal claimedAmount;

	public UUID getPolicyId() {
		return policyId;
	}

	public void setPolicyId(UUID policyId) {
		this.policyId = policyId;
	}

	public LocalDate getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(LocalDate treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public String getIcd10Code() {
		return icd10Code;
	}

	public void setIcd10Code(String icd10Code) {
		this.icd10Code = icd10Code;
	}

	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public BigDecimal getClaimedAmount() {
		return claimedAmount;
	}

	public void setClaimedAmount(BigDecimal claimedAmount) {
		this.claimedAmount = claimedAmount;
	}
}