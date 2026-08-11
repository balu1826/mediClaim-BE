package com.mediclaim.mediclaim.fraud;


import com.mediclaim.mediclaim.entity.Claim;

import java.util.concurrent.CompletableFuture;

public interface IFraudScoringService {

    CompletableFuture<Integer> calculateScore(Claim claim);
}