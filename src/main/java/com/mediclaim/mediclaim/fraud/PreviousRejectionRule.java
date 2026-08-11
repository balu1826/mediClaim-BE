package com.mediclaim.mediclaim.fraud;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimStatus;
import com.mediclaim.mediclaim.repository.ClaimRepository;

@Component
public class PreviousRejectionRule implements FraudRule {

	private static final int SCORE = 20;

	private final ClaimRepository claimRepository;

	public PreviousRejectionRule(ClaimRepository claimRepository) {

		this.claimRepository = claimRepository;
	}

	@Override
	public int evaluate(Claim claim) {

		LocalDate currentDate = claim.getTreatmentDate();

		LocalDate fromDate = currentDate.minusDays(90);

		long rejectedClaims = claimRepository.countPreviousRejectedClaims(claim.getPatientId(), claim.getIcd10Code(),
				ClaimStatus.REJECTED, fromDate, currentDate);

		return rejectedClaims > 0 ? SCORE : 0;
	}
}