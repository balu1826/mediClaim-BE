package com.mediclaim.mediclaim.annotaion;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

	private final AuditService auditService;

	public AuditAspect(AuditService auditService) {
		this.auditService = auditService;
	}

	@Around("@annotation(auditable)")
	public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

		try {

			Object result = joinPoint.proceed();

			auditService.recordSuccess(joinPoint, auditable);

			return result;

		} catch (Exception exception) {

			auditService.recordFailure(joinPoint, auditable, exception);

			throw exception;
		}
	}
}