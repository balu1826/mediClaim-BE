package com.mediclaim.mediclaim.annotaion;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

	@Override
	public void recordSuccess(ProceedingJoinPoint joinPoint, Auditable auditable) {

		// Persist audit record
	}

	@Override
	public void recordFailure(ProceedingJoinPoint joinPoint, Auditable auditable, Exception exception) {

		// Persist failed audit record
	}
}