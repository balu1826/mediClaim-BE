package com.mediclaim.mediclaim.fraud;

import java.time.DayOfWeek;

import org.springframework.stereotype.Component;

import com.mediclaim.mediclaim.entity.Claim;

@Component
public class TreatmentDateRule implements FraudRule {

	private static final int SCORE = 20;

	@Override
	public int evaluate(Claim claim) {

		DayOfWeek day = claim.getTreatmentDate().getDayOfWeek();

		boolean holiday = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;

		return holiday ? SCORE : 0;
	}
}