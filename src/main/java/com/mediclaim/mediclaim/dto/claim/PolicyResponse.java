package com.mediclaim.mediclaim.dto.claim;

import com.mediclaim.mediclaim.entity.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PolicyResponse {

    private UUID id;
    private String policyNumber;
    private String policyType;
    private BigDecimal annualLimit;
    private BigDecimal usedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;

    public PolicyResponse(
            UUID id,
            String policyNumber,
            String policyType,
            BigDecimal annualLimit,
            BigDecimal usedAmount,
            LocalDate startDate,
            LocalDate endDate,
            PolicyStatus status) {

        this.id = id;
        this.policyNumber = policyNumber;
        this.policyType = policyType;
        this.annualLimit = annualLimit;
        this.usedAmount = usedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getPolicyType() {
        return policyType;
    }

    public BigDecimal getAnnualLimit() {
        return annualLimit;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
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
}