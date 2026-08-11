package com.mediclaim.mediclaim.fraud;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.repository.ClaimRepository;

@Component
public class AmountDeviationRule implements FraudRule {

	private static final int SCORE = 20;

	private final ClaimRepository claimRepository;

	public AmountDeviationRule(ClaimRepository claimRepository) {

		this.claimRepository = claimRepository;
	}

	@Override
	public int evaluate(Claim claim) {

		BigDecimal medianApprovedAmount = claimRepository.findMedianApprovedAmount(claim.getTenantId(),
				claim.getIcd10Code());

		if (medianApprovedAmount == null) {
			return 0;
		}

		BigDecimal threshold = medianApprovedAmount.multiply(BigDecimal.valueOf(1.5));

		return claim.getClaimedAmount().compareTo(threshold) > 0 ? SCORE : 0;
	}
}