package com.mediclaim.mediclaim.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.mediclaim.mediclaim.exception.InvalidClaimTransitionException;

@Entity
@Table(name = "claims", indexes = { @Index(name = "idx_claim_tenant", columnList = "tenant_id"),
		@Index(name = "idx_claim_patient", columnList = "patient_id"),
		@Index(name = "idx_claim_policy", columnList = "policy_id"),
		@Index(name = "idx_claim_status", columnList = "status") })
public class Claim {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@Column(name = "patient_id", nullable = false)
	private UUID patientId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "policy_id", nullable = false)
	private Policy policy;

	@Column(name = "treatment_date", nullable = false)
	private LocalDate treatmentDate;

	@Column(name = "icd10_code", nullable = false, length = 20)
	private String icd10Code;

	@Column(name = "provider_id", nullable = false)
	private UUID providerId;

	@Column(name = "claimed_amount", nullable = false, precision = 18, scale = 2)
	private BigDecimal claimedAmount;

	@Column(name = "approved_amount", precision = 18, scale = 2)
	private BigDecimal approvedAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ClaimStatus status;

	@Column(name = "fraud_score")
	private Integer fraudScore;

	@Column(name = "fraud_flagged", nullable = false)
	private boolean fraudFlagged;

	@Column(name = "assigned_officer_id")
	private UUID assignedOfficerId;

	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;
	@Column(length = 1000)
	private String documentRequestReason;
	@Version
	@Column(nullable = false)
	private Long version;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {

		LocalDateTime now = LocalDateTime.now();

		createdAt = now;
		updatedAt = now;

		status = ClaimStatus.DRAFT;
		fraudFlagged = false;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
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

	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(BigDecimal approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public ClaimStatus getStatus() {
		return status;
	}

	public void setStatus(ClaimStatus status) {
		this.status = status;
	}

	public Integer getFraudScore() {
		return fraudScore;
	}

	public void setFraudScore(Integer fraudScore) {
		this.fraudScore = fraudScore;
	}

	public boolean isFraudFlagged() {
		return fraudFlagged;
	}

	public void setFraudFlagged(boolean fraudFlagged) {
		this.fraudFlagged = fraudFlagged;
	}

	public UUID getAssignedOfficerId() {
		return assignedOfficerId;
	}

	public void setAssignedOfficerId(UUID assignedOfficerId) {
		this.assignedOfficerId = assignedOfficerId;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	

	public String getDocumentRequestReason() {
		return documentRequestReason;
	}

	public void setDocumentRequestReason(String documentRequestReason) {
		this.documentRequestReason = documentRequestReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void transitionTo(ClaimStatus newStatus) {

	    if (!isValidTransition(newStatus)) {
	        throw new InvalidClaimTransitionException(
	                "Cannot move claim from "
	                        + status
	                        + " to "
	                        + newStatus
	        );
	    }

	    this.status = newStatus;
	}
	private boolean isValidTransition(
	        ClaimStatus newStatus) {

	    return switch (status) {

	        case DRAFT ->
	                newStatus == ClaimStatus.SUBMITTED
	                || newStatus == ClaimStatus.WITHDRAWN;

	        case SUBMITTED ->
	                newStatus == ClaimStatus.UNDER_REVIEW
	                || newStatus == ClaimStatus.PENDING_ASSIGNMENT;

	        case PENDING_ASSIGNMENT ->
	                newStatus == ClaimStatus.UNDER_REVIEW;

	        case UNDER_REVIEW ->
	                newStatus == ClaimStatus.PENDING_DOCUMENTS
	                || newStatus == ClaimStatus.APPROVED
	                || newStatus == ClaimStatus.REJECTED
	                || newStatus == ClaimStatus.ESCALATED;

	        case PENDING_DOCUMENTS ->
	                newStatus == ClaimStatus.UNDER_REVIEW
	                || newStatus == ClaimStatus.REJECTED;

	        case ESCALATED ->
	                newStatus == ClaimStatus.APPROVED
	                || newStatus == ClaimStatus.REJECTED;

	        case APPROVED ->
	                newStatus == ClaimStatus.SETTLED;

	        case SETTLED, REJECTED, WITHDRAWN ->
	                false;
	    };
	}
	
}