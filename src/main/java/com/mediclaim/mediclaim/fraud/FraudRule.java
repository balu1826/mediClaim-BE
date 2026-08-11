package com.mediclaim.mediclaim.fraud;

import com.mediclaim.mediclaim.entity.Claim;

public interface FraudRule {

    int evaluate(Claim claim);
}