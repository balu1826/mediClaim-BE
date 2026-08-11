package com.mediclaim.mediclaim.dto.claim;

import com.mediclaim.mediclaim.entity.PolicyTypeStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class PolicyTypeResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal premium;
    private BigDecimal annualLimit;
    private String coverageCategories;
    private PolicyTypeStatus status;

    public PolicyTypeResponse(
            UUID id,
            String name,
            String description,
            BigDecimal premium,
            BigDecimal annualLimit,
            String coverageCategories,
            PolicyTypeStatus status) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.premium = premium;
        this.annualLimit = annualLimit;
        this.coverageCategories = coverageCategories;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public BigDecimal getAnnualLimit() {
        return annualLimit;
    }

    public String getCoverageCategories() {
        return coverageCategories;
    }

    public PolicyTypeStatus getStatus() {
        return status;
    }
}