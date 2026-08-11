package com.mediclaim.mediclaim.fraud;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.mediclaim.mediclaim.entity.Claim;

@Component
public class EarlyEnrollmentRule implements FraudRule {

	private static final int SCORE = 15;

	@Override
	public int evaluate(Claim claim) {

		LocalDateTime enrolledAt = claim.getPolicy().getCreatedAt();

		LocalDateTime submittedAt = LocalDateTime.now();

		if (enrolledAt == null || submittedAt == null) {
			return 0;
		}

		Duration duration = Duration.between(enrolledAt, submittedAt);

		return duration.toHours() < 24 ? SCORE : 0;
	}
}