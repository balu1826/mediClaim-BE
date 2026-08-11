package com.mediclaim.mediclaim.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediclaim.mediclaim.dto.claim.EnrollPolicyRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyResponse;
import com.mediclaim.mediclaim.dto.claim.PolicyUpgradeResponse;
import com.mediclaim.mediclaim.entity.Policy;
import com.mediclaim.mediclaim.entity.PolicyStatus;
import com.mediclaim.mediclaim.entity.PolicyType;
import com.mediclaim.mediclaim.entity.PolicyTypeStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;
import com.mediclaim.mediclaim.repository.ClaimRepository;
import com.mediclaim.mediclaim.repository.PolicyRepository;
import com.mediclaim.mediclaim.repository.PolicyTypeRepository;
import com.mediclaim.mediclaim.security.SecurityUtils;

@Service
public class PolicyService {
	private final PolicyTypeRepository policyTypeRepository;
	private final PolicyRepository policyRepository;
	private final ClaimRepository claimRepository;

	public PolicyService(PolicyRepository policyRepository, PolicyTypeRepository policyTypeRepository,
			ClaimRepository claimRepository) {

		this.policyRepository = policyRepository;
		this.policyTypeRepository = policyTypeRepository;
		this.claimRepository = claimRepository;
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

	@Transactional
	public PolicyUpgradeResponse upgradePolicy(UUID policyId, UUID targetPolicyTypeId) {

		UUID userId = SecurityUtils.getCurrentUserId();

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Policy policy = policyRepository.findById(policyId)
				.orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

		/*
		 * Tenant isolation
		 */
		if (!policy.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Policy not found");
		}

		/*
		 * Patient ownership
		 */
		if (!policy.getPatientId().equals(userId)) {
			throw new AccessDeniedException("You can upgrade only your own policy");
		}

		/*
		 * Policy must be active.
		 */
		if (policy.getStatus() != PolicyStatus.ACTIVE) {
			throw new BusinessException("Only active policies can be upgraded");
		}

		LocalDate upgradeDate = LocalDate.now();

		/*
		 * Upgrade must happen during the policy period.
		 */
		if (upgradeDate.isBefore(policy.getStartDate()) || upgradeDate.isAfter(policy.getEndDate())) {

			throw new BusinessException("Policy is outside its active period");
		}

		PolicyType oldPolicyType = policy.getPolicyType();

		PolicyType newPolicyType = policyTypeRepository.findById(targetPolicyTypeId)
				.orElseThrow(() -> new ResourceNotFoundException("Target policy type not found"));

		/*
		 * Target plan must belong to the same tenant.
		 */
		if (!newPolicyType.getTenant().getId().equals(tenantId)) {

			throw new ResourceNotFoundException("Target policy type not found");
		}

		/*
		 * Target policy type must be active.
		 */
		if (newPolicyType.getStatus() != PolicyTypeStatus.ACTIVE) {

			throw new BusinessException("Target policy type is not active");
		}

		/*
		 * Only upgrades are allowed.
		 */
		if (newPolicyType.getAnnualLimit().compareTo(oldPolicyType.getAnnualLimit()) <= 0) {

			throw new BusinessException("Target policy must have a higher annual limit");
		}

		/*
		 * Calculate remaining period.
		 */
		long remainingDays = ChronoUnit.DAYS.between(upgradeDate, policy.getEndDate()) + 1;

		long totalDays = ChronoUnit.DAYS.between(policy.getStartDate(), policy.getEndDate()) + 1;

		/*
		 * Premium difference between plans.
		 */
		BigDecimal premiumDifference = newPolicyType.getPremium().subtract(oldPolicyType.getPremium());

		/*
		 * Prorated additional premium.
		 */
		BigDecimal additionalPremium = premiumDifference.multiply(BigDecimal.valueOf(remainingDays))
				.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		/*
		 * Coverage delta.
		 *
		 * The additional annual coverage available from the upgrade date.
		 */
		BigDecimal coverageDelta = newPolicyType.getAnnualLimit().subtract(oldPolicyType.getAnnualLimit());

		/*
		 * Apply upgrade.
		 */
		policy.setPolicyType(newPolicyType);

		policy.setAnnualLimit(newPolicyType.getAnnualLimit());

		policy.setEndDate(upgradeDate);

		policyRepository.save(policy);

		return new PolicyUpgradeResponse(policy.getId(), policy.getPolicyNumber(), oldPolicyType.getName(),
				newPolicyType.getName(), oldPolicyType.getAnnualLimit(), newPolicyType.getAnnualLimit(),
				additionalPremium, upgradeDate);
	}

	@Transactional
	public PolicyResponse renewPolicy(UUID policyId) {

		UUID userId = SecurityUtils.getCurrentUserId();

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Policy oldPolicy = policyRepository.findById(policyId)
				.orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

		/*
		 * Tenant isolation
		 */
		if (!oldPolicy.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Policy not found");
		}

		/*
		 * Patient can renew only their own policy.
		 */
		if (!oldPolicy.getPatientId().equals(userId)) {
			throw new AccessDeniedException("You can renew only your own policy");
		}

		/*
		 * Policy must have reached its renewal period.
		 */
		if (LocalDate.now().isBefore(oldPolicy.getEndDate())) {

			throw new BusinessException("Policy cannot be renewed before its expiry date");
		}

		/*
		 * Create new policy.
		 */
		Policy newPolicy = new Policy();

		newPolicy.setTenantId(oldPolicy.getTenantId());

		newPolicy.setPatientId(oldPolicy.getPatientId());

		newPolicy.setPolicyType(oldPolicy.getPolicyType());

		newPolicy.setAnnualLimit(oldPolicy.getPolicyType().getAnnualLimit());

		newPolicy.setUsedAmount(BigDecimal.ZERO);

		newPolicy.setStartDate(oldPolicy.getEndDate().plusDays(1));

		newPolicy.setEndDate(oldPolicy.getEndDate().plusYears(1));

		newPolicy.setStatus(PolicyStatus.ACTIVE);

		/*
		 * Generate a NEW policy number.
		 */
		newPolicy.setPolicyNumber(

				generatePolicyNumber(tenantId, oldPolicy.getPolicyType().getTenant().getCode()));

		Policy savedPolicy = policyRepository.save(newPolicy);

		return mapToResponse(savedPolicy);
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