package com.mediclaim.mediclaim.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediclaim.mediclaim.dto.claim.EnrollPolicyRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyResponse;
import com.mediclaim.mediclaim.entity.Policy;
import com.mediclaim.mediclaim.entity.PolicyStatus;
import com.mediclaim.mediclaim.entity.PolicyType;
import com.mediclaim.mediclaim.entity.PolicyTypeStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;
import com.mediclaim.mediclaim.repository.PolicyRepository;
import com.mediclaim.mediclaim.repository.PolicyTypeRepository;

@Service
public class PolicyService {
	private final PolicyTypeRepository policyTypeRepository;
	private final PolicyRepository policyRepository;

	public PolicyService(PolicyRepository policyRepository, PolicyTypeRepository policyTypeRepository) {

		this.policyRepository = policyRepository;
		this.policyTypeRepository = policyTypeRepository;
	}

	@Transactional
	public PolicyResponse enrollPolicy(EnrollPolicyRequest request) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID patientId = UUID.fromString(authentication.getToken().getSubject());

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		PolicyType policyType = policyTypeRepository.findById(request.getPolicyTypeId())
				.orElseThrow(() -> new ResourceNotFoundException("Policy type not found"));

		/*
		 * Tenant isolation
		 */
		if (!policyType.getTenant().getId().equals(tenantId)) {

			throw new ResourceNotFoundException("Policy type not found");
		}

		if (policyType.getStatus() != PolicyTypeStatus.ACTIVE) {

			throw new BusinessException("Policy type is not active");
		}

		/*
		 * Prevent duplicate enrollment
		 */
		if (policyRepository.existsByTenantIdAndPatientIdAndPolicyTypeId(tenantId, patientId,
				request.getPolicyTypeId())) {

			throw new BusinessException("You are already enrolled in this policy");
		}

		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusYears(1);

		Policy policy = new Policy();

		policy.setTenantId(tenantId);
		policy.setPatientId(patientId);
		policy.setPolicyType(policyType);

		policy.setAnnualLimit(policyType.getAnnualLimit());

		policy.setUsedAmount(BigDecimal.ZERO);

		policy.setStartDate(startDate);
		policy.setEndDate(endDate);

		policy.setStatus(PolicyStatus.ACTIVE);

		policy.setPolicyNumber(generatePolicyNumber(tenantId, policyType.getTenant().getCode()));

		Policy savedPolicy = policyRepository.save(policy);

		return new PolicyResponse(savedPolicy.getId(), savedPolicy.getPolicyNumber(),
				savedPolicy.getPolicyType().getName(), savedPolicy.getAnnualLimit(), savedPolicy.getUsedAmount(),
				savedPolicy.getStartDate(), savedPolicy.getEndDate(), savedPolicy.getStatus());
	}

	@Transactional(readOnly = true)
	public List<PolicyResponse> getAllPolicies() {

		return policyRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public PolicyResponse getPolicyByNumber(String policyNumber) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		Policy policy = policyRepository.findByTenantIdAndPolicyNumber(tenantId, policyNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

		return mapToResponse(policy);
	}

	private String generatePolicyNumber(UUID tenantId, String tenantCode) {

		long sequence = policyRepository.countByTenantId(tenantId) + 1;

		return String.format("POL-%d-%s-%06d", LocalDate.now().getYear(), tenantCode, sequence);
	}

	private PolicyResponse mapToResponse(Policy policy) {
		return new PolicyResponse(policy.getId(), policy.getPolicyNumber(), policy.getPolicyType().getName(),
				policy.getAnnualLimit(), policy.getUsedAmount(), policy.getStartDate(), policy.getEndDate(),
				policy.getStatus());
	}

}