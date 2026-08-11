package com.mediclaim.mediclaim.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "policies", uniqueConstraints = {
		@UniqueConstraint(name = "uk_policy_number", columnNames = "policy_number") })
public class Policy {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "tenant_id", nullable = false)
	private UUID tenantId;

	@Column(name = "patient_id", nullable = false)
	private UUID patientId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "policy_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_policy_policy_type"))
	private PolicyType policyType;

	@Column(name = "policy_number", nullable = false, unique = true, length = 30)
	private String policyNumber;

	@Column(name = "annual_limit", nullable = false, precision = 18, scale = 2)
	private BigDecimal annualLimit;

	@Column(name = "used_amount", nullable = false, precision = 18, scale = 2)
	private BigDecimal usedAmount;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PolicyStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	// Getters and Setters

	public UUID getId() {
		return id;
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

	public PolicyType getPolicyType() {
		return policyType;
	}

	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public BigDecimal getAnnualLimit() {
		return annualLimit;
	}

	public void setAnnualLimit(BigDecimal annualLimit) {
		this.annualLimit = annualLimit;
	}

	public BigDecimal getUsedAmount() {
		return usedAmount;
	}

	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public PolicyStatus getStatus() {
		return status;
	}

	public void setStatus(PolicyStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setId(UUID id) {
		this.id = id;
	}

}