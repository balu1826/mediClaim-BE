package com.mediclaim.mediclaim.fraud;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.repository.ClaimRepository;

@Component
public class ClaimFrequencyRule implements FraudRule {

	private static final int SCORE = 25;

	private final ClaimRepository claimRepository;

	public ClaimFrequencyRule(ClaimRepository claimRepository) {

		this.claimRepository = claimRepository;
	}

	@Override
	public int evaluate(Claim claim) {

		LocalDate fromDate = claim.getTreatmentDate().minusDays(30);

		LocalDate toDate = claim.getTreatmentDate();

		long claimCount = claimRepository.countClaimsByPatientAndIcd10(claim.getPatientId(), claim.getIcd10Code(),
				fromDate, toDate);

		return claimCount > 3 ? SCORE : 0;
	}
}