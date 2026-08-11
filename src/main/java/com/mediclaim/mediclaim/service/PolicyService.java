package com.mediclaim.mediclaim.service;

import com.mediclaim.mediclaim.dto.claim.CreatePolicyRequest;
import com.mediclaim.mediclaim.dto.claim.PolicyResponse;
import com.mediclaim.mediclaim.dto.tenant.TenantResponse;
import com.mediclaim.mediclaim.entity.Policy;
import com.mediclaim.mediclaim.entity.PolicyStatus;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.entity.TenantStatus;
import com.mediclaim.mediclaim.repository.PolicyRepository;
import com.mediclaim.mediclaim.repository.TenantRepository;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolicyService {

	private final PolicyRepository policyRepository;
	private final TenantRepository tenantRepository;

	public PolicyService(PolicyRepository policyRepository, TenantRepository tenantRepository) {

		this.policyRepository = policyRepository;
		this.tenantRepository = tenantRepository;
	}

	@Transactional
	public PolicyResponse createPolicy(CreatePolicyRequest request) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		String tenantIdClaim = authentication.getToken().getClaimAsString("tenantId");
		UUID tenantId = UUID.fromString(tenantIdClaim);
		Tenant tenant = tenantRepository.findById(tenantId)
				.orElseThrow(() -> new BusinessException("Tenant not found"));
		if (tenant.getStatus() != TenantStatus.ACTIVE) {
			throw new BusinessException("Tenant is not active");
		}
		String code = request.getCode().trim().toUpperCase();

		if (policyRepository.existsByTenantIdAndCode(tenantId, code)) {
			throw new BusinessException("Policy code already exists in this tenant", HttpStatus.CONFLICT);
		}

		if (!request.getEndDate().isAfter(request.getStartDate())) {

			throw new BusinessException("Policy end date must be after start date");
		}

		Policy policy = new Policy();

		policy.setName(request.getName().trim());
		policy.setCode(code);
		policy.setDescription(request.getDescription());
		policy.setCoverageAmount(request.getCoverageAmount());
		policy.setPremium(request.getPremium());
		policy.setStartDate(request.getStartDate());
		policy.setEndDate(request.getEndDate());

		// Server-controlled values
		policy.setStatus(PolicyStatus.ACTIVE);
		policy.setTenant(tenant);

		Policy savedPolicy= policyRepository.save(policy);
		return mapToResponse(savedPolicy);
	}
	
	@Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicies() {

        return policyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
	@Transactional(readOnly = true)
    public PolicyResponse getPolicy(String code) {
		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();
		String tenantIdClaim = authentication.getToken().getClaimAsString("tenantId");
		UUID tenantId = UUID.fromString(tenantIdClaim);
		Policy policy=policyRepository.findByCode(code)
				.orElseThrow(() ->new ResourceNotFoundException("Policy not found" ,HttpStatus.NOT_FOUND));;
		if(policyRepository.existsByTenantIdAndCode(tenantId, code)) {
			throw new ResourceNotFoundException("No Active Tenant Found With This Code!",HttpStatus.NOT_FOUND);
		}
				
        return mapToResponse(policy);
    }
	
	
	private PolicyResponse mapToResponse(Policy policy) {
		return new  PolicyResponse(policy.getId(), policy.getName(), policy.getCode(),
				policy.getDescription(), policy.getCoverageAmount(), policy.getPremium(), policy.getStartDate(),
				policy.getEndDate(), policy.getStatus(), policy.getCreatedAt());
	}
}