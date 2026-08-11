package com.mediclaim.mediclaim.dto.claim;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreatePolicyTypeRequest {

    @NotBlank(message = "Policy type name is required")
    @Size(max = 100, message = "Policy type name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Premium is required")
    @DecimalMin(
            value = "0.01",
            message = "Premium must be greater than zero"
    )
    private BigDecimal premium;

    @NotNull(message = "Annual limit is required")
    @DecimalMin(
            value = "0.01",
            message = "Annual limit must be greater than zero"
    )
    private BigDecimal annualLimit;

    @NotBlank(message = "Coverage categories are required")
    private String coverageCategories;

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
}