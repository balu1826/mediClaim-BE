package com.mediclaim.mediclaim.fraud;

import com.mediclaim.mediclaim.entity.Claim;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class FraudScoringService implements IFraudScoringService {

	private final List<FraudRule> fraudRules;
	private final Executor fraudTaskExecutor;

	public FraudScoringService(List<FraudRule> fraudRules, @Qualifier("fraudTaskExecutor") Executor fraudTaskExecutor) {

		this.fraudRules = fraudRules;
		this.fraudTaskExecutor = fraudTaskExecutor;
	}

	@Override
	public CompletableFuture<Integer> calculateScore(Claim claim) {

		List<CompletableFuture<Integer>> futures = fraudRules.stream()
				.map(rule -> CompletableFuture.supplyAsync(() -> rule.evaluate(claim), fraudTaskExecutor)).toList();

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
				.thenApply(ignored -> futures.stream().mapToInt(CompletableFuture::join).sum());
	}
}