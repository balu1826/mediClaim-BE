package com.mediclaim.mediclaim.service;

import com.mediclaim.mediclaim.dto.claim.CreatePolicyTypeRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyTypeResponse;
import com.mediclaim.mediclaim.entity.PolicyType;
import com.mediclaim.mediclaim.entity.PolicyTypeStatus;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;
import com.mediclaim.mediclaim.repository.PolicyTypeRepository;
import com.mediclaim.mediclaim.repository.TenantRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PolicyTypeService {

	private final PolicyTypeRepository policyTypeRepository;
	private final TenantRepository tenantRepository;

	public PolicyTypeService(PolicyTypeRepository policyTypeRepository, TenantRepository tenantRepository) {

		this.policyTypeRepository = policyTypeRepository;
		this.tenantRepository = tenantRepository;
	}

	@Transactional
	public PolicyTypeResponse createPolicyType(CreatePolicyTypeRequest request) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		Tenant tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

		String name = request.getName().trim();

		if (policyTypeRepository.existsByTenantIdAndName(tenantId, name)) {

			throw new BusinessException("Policy type already exists");
		}

		PolicyType policyType = new PolicyType();

		policyType.setName(name);
		policyType.setDescription(request.getDescription());
		policyType.setPremium(request.getPremium());
		policyType.setAnnualLimit(request.getAnnualLimit());
		policyType.setCoverageCategories(request.getCoverageCategories());

		// Server-controlled values
		policyType.setStatus(PolicyTypeStatus.ACTIVE);

		policyType.setTenant(tenant);

		PolicyType saved = policyTypeRepository.save(policyType);

		return mapToResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<PolicyTypeResponse> getAvailablePolicyTypes() {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		return policyTypeRepository.findByTenantIdAndStatus(tenantId, PolicyTypeStatus.ACTIVE).stream()
				.map(policyType -> new PolicyTypeResponse(policyType.getId(), policyType.getName(),
						policyType.getDescription(), policyType.getPremium(), policyType.getAnnualLimit(),
						policyType.getCoverageCategories(), policyType.getStatus()))
				.toList();
	}

	// Helper for PolicyType
	private PolicyTypeResponse mapToResponse(PolicyType policyType) {
		return new PolicyTypeResponse(policyType.getId(), policyType.getName(), policyType.getDescription(),
				policyType.getPremium(), policyType.getAnnualLimit(), policyType.getCoverageCategories(),
				policyType.getStatus());
	}
}