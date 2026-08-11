package com.mediclaim.mediclaim.dto.claim;

import com.mediclaim.mediclaim.entity.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class PolicyResponse {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private BigDecimal coverageAmount;
    private BigDecimal premium;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;
    private LocalDateTime createdAt;

    public PolicyResponse(
            UUID id,
            String name,
            String code,
            String description,
            BigDecimal coverageAmount,
            BigDecimal premium,
            LocalDate startDate,
            LocalDate endDate,
            PolicyStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.coverageAmount = coverageAmount;
        this.premium = premium;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}