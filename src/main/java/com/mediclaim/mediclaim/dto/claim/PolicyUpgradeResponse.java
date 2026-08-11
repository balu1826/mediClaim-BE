package com.mediclaim.mediclaim.dto.claim;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PolicyUpgradeResponse {

	private UUID policyId;
	private String policyNumber;

	private String oldPolicyType;
	private String newPolicyType;

	private BigDecimal oldAnnualLimit;
	private BigDecimal newAnnualLimit;

	private BigDecimal additionalPremium;

	private LocalDate upgradeDate;

	public PolicyUpgradeResponse(UUID policyId, String policyNumber, String oldPolicyType, String newPolicyType,
			BigDecimal oldAnnualLimit, BigDecimal newAnnualLimit, BigDecimal additionalPremium, LocalDate upgradeDate) {

		this.policyId = policyId;
		this.policyNumber = policyNumber;
		this.oldPolicyType = oldPolicyType;
		this.newPolicyType = newPolicyType;
		this.oldAnnualLimit = oldAnnualLimit;
		this.newAnnualLimit = newAnnualLimit;
		this.additionalPremium = additionalPremium;
		this.upgradeDate = upgradeDate;
	}

	public UUID getPolicyId() {
		return policyId;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public String getOldPolicyType() {
		return oldPolicyType;
	}

	public String getNewPolicyType() {
		return newPolicyType;
	}

	public BigDecimal getOldAnnualLimit() {
		return oldAnnualLimit;
	}

	public BigDecimal getNewAnnualLimit() {
		return newAnnualLimit;
	}

	public BigDecimal getAdditionalPremium() {
		return additionalPremium;
	}

	public LocalDate getUpgradeDate() {
		return upgradeDate;
	}
}
