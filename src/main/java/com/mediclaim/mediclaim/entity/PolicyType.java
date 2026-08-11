package com.mediclaim.mediclaim.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "policy_types")
public class PolicyType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal premium;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal annualLimit;

    @Column(nullable = false, length = 500)
    private String coverageCategories;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyTypeStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
    @Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPremium() {
		return premium;
	}

	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}

	public BigDecimal getAnnualLimit() {
		return annualLimit;
	}

	public void setAnnualLimit(BigDecimal annualLimit) {
		this.annualLimit = annualLimit;
	}

	public String getCoverageCategories() {
		return coverageCategories;
	}

	public void setCoverageCategories(String coverageCategories) {
		this.coverageCategories = coverageCategories;
	}

	public PolicyTypeStatus getStatus() {
		return status;
	}

	public void setStatus(PolicyTypeStatus status) {
		this.status = status;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

    // getters and setters
    
    
}