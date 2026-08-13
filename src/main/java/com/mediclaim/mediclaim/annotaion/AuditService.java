package com.mediclaim.mediclaim.annotaion;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AuditService {

	void recordSuccess(ProceedingJoinPoint joinPoint, Auditable auditable);

	void recordFailure(ProceedingJoinPoint joinPoint, Auditable auditable, Exception exception);
}