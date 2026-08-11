package com.mediclaim.mediclaim.dto.claim;

import com.mediclaim.mediclaim.entity.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ClaimResponse {

	private UUID id;
	private UUID policyId;
	private LocalDate treatmentDate;
	private String icd10Code;
	private UUID providerId;
	private BigDecimal claimedAmount;
	private BigDecimal approvedAmount;
	private ClaimStatus status;
	private Integer fraudScore;
	private boolean fraudFlagged;

	public ClaimResponse(UUID id, UUID policyId, LocalDate treatmentDate, String icd10Code, UUID providerId,
			BigDecimal claimedAmount, BigDecimal approvedAmount, ClaimStatus status, Integer fraudScore,
			boolean fraudFlagged) {

		this.id = id;
		this.policyId = policyId;
		this.treatmentDate = treatmentDate;
		this.icd10Code = icd10Code;
		this.providerId = providerId;
		this.claimedAmount = claimedAmount;
		this.approvedAmount = approvedAmount;
		this.status = status;
		this.fraudScore = fraudScore;
		this.fraudFlagged = fraudFlagged;
	}

	public UUID getId() {
		return id;
	}

	public UUID getPolicyId() {
		return policyId;
	}

	public LocalDate getTreatmentDate() {
		return treatmentDate;
	}

	public String getIcd10Code() {
		return icd10Code;
	}

	public UUID getProviderId() {
		return providerId;
	}

	public BigDecimal getClaimedAmount() {
		return claimedAmount;
	}

	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	public ClaimStatus getStatus() {
		return status;
	}

	public Integer getFraudScore() {
		return fraudScore;
	}

	public boolean isFraudFlagged() {
		return fraudFlagged;
	}
}