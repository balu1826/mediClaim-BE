package com.mediclaim.mediclaim.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediclaim.mediclaim.dto.claim.ClaimResponse;
import com.mediclaim.mediclaim.dto.claim.CreateClaimDraftRequest;
import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimDocument;
import com.mediclaim.mediclaim.entity.ClaimStatus;
import com.mediclaim.mediclaim.entity.Policy;
import com.mediclaim.mediclaim.entity.PolicyStatus;
import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;
import com.mediclaim.mediclaim.fraud.FraudScoringService;
import com.mediclaim.mediclaim.repository.ClaimDocumentRepository;
import com.mediclaim.mediclaim.repository.ClaimRepository;
import com.mediclaim.mediclaim.repository.PolicyRepository;
import com.mediclaim.mediclaim.repository.UserRepository;
import com.mediclaim.mediclaim.security.SecurityUtils;

@Service
public class ClaimService {

	private final ClaimRepository claimRepository;
	private final ClaimDocumentRepository claimDocumentRepository;
	private final PolicyRepository policyRepository;
	private final FraudScoringService fraudScoringService;
	private final ClaimAssignmentService claimAssignmentService;
	private final UserRepository userRepository;

	public ClaimService(ClaimRepository claimRepository, ClaimDocumentRepository claimDocumentRepository,
			PolicyRepository policyRepository, FraudScoringService fraudScoreService,
			ClaimAssignmentService claimAssignmentService, UserRepository userRepository) {

		this.claimRepository = claimRepository;
		this.claimDocumentRepository = claimDocumentRepository;
		this.policyRepository = policyRepository;
		this.claimAssignmentService = claimAssignmentService;
		this.fraudScoringService = fraudScoreService;
		this.userRepository = userRepository;
	}

	@Transactional
	public ClaimResponse createDraft(CreateClaimDraftRequest request) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID patientId = UUID.fromString(authentication.getToken().getSubject());

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		Policy policy = policyRepository.findById(request.getPolicyId())
				.orElseThrow(() -> new ResourceNotFoundException("Policy not found"));

		/*
		 * Tenant isolation
		 */
		if (!policy.getTenantId().equals(tenantId)) {

			throw new ResourceNotFoundException("Policy not found");
		}

		/*
		 * Patient can create a claim only against their own policy.
		 */
		if (!policy.getPatientId().equals(patientId)) {

			throw new ResourceNotFoundException("Policy not found");
		}

		Claim claim = new Claim();

		claim.setTenantId(tenantId);
		claim.setPatientId(patientId);
		claim.setPolicy(policy);
		claim.setTreatmentDate(request.getTreatmentDate());
		claim.setIcd10Code(request.getIcd10Code());
		claim.setProviderId(request.getProviderId());
		claim.setClaimedAmount(request.getClaimedAmount());

		claim.setStatus(ClaimStatus.DRAFT);
		claim.setFraudFlagged(false);

		Claim savedClaim = claimRepository.save(claim);

		return maptoResponse(savedClaim);
	}

	@Transactional
	public ClaimResponse submitClaim(UUID claimId) {

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID patientId = UUID.fromString(authentication.getToken().getSubject());

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		/*
		 * Tenant isolation
		 */
		if (!claim.getTenantId().equals(tenantId)) {

			throw new ResourceNotFoundException("Claim not found");
		}

		/*
		 * Patient can only submit their own claim.
		 */
		if (!claim.getPatientId().equals(patientId)) {

			throw new ResourceNotFoundException("Claim not found");
		}

		/*
		 * Only DRAFT claims can be submitted.
		 */
		if (claim.getStatus() != ClaimStatus.DRAFT) {

			throw new BusinessException("Only draft claims can be submitted");
		}

		Policy policy = claim.getPolicy();

		/*
		 * Policy must be active.
		 */
		if (policy.getStatus() != PolicyStatus.ACTIVE) {

			throw new BusinessException("Policy is not active");
		}

		/*
		 * Treatment date must be within policy validity period.
		 */
		if (claim.getTreatmentDate().isBefore(policy.getStartDate())
				|| claim.getTreatmentDate().isAfter(policy.getEndDate())) {

			throw new BusinessException("Treatment date is outside policy coverage period");
		}

		/*
		 * Calculate remaining policy balance.
		 */
		BigDecimal remainingAmount = policy.getAnnualLimit().subtract(policy.getUsedAmount());

		if (claim.getClaimedAmount().compareTo(remainingAmount) > 0) {

			throw new BusinessException("Insufficient policy balance");
		}

		/*
		 * Mark submission time.
		 */
		claim.setCreatedAt(LocalDateTime.now());

		/*
		 * Fraud scoring.
		 *
		 * All five rules execute concurrently.
		 */
		Integer fraudScore = fraudScoringService.calculateScore(claim).join();

		claim.setFraudScore(fraudScore);

		claim.setFraudFlagged(fraudScore >= 60);

		claim.setStatus(ClaimStatus.SUBMITTED);

		/*
		 * Persist fraud score BEFORE officer assignment.
		 */
		Claim savedClaim = claimRepository.save(claim);

		/*
		 * Assign only after fraud score has been persisted.
		 */
		return maptoResponse(claimAssignmentService.assignClaim(savedClaim));
	}

	@Transactional
	public ClaimResponse transitionClaim(UUID claimId, ClaimStatus targetStatus) {

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		UUID userId = UUID.fromString(authentication.getToken().getSubject());

		UUID tenantId = UUID.fromString(authentication.getToken().getClaimAsString("tenantId"));

		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		/*
		 * ClaimsOfficer can only operate on claims assigned to them.
		 */
		if (currentUser.getRole() == Role.CLAIMS_OFFICER && !userId.equals(claim.getAssignedOfficerId())) {

			throw new AccessDeniedException("Claim is not assigned to you");
		}

		claim.transitionTo(targetStatus);

		claimRepository.save(claim);

		return maptoResponse(claim);
	}

	@Transactional
	public ClaimResponse approveClaim(UUID claimId, BigDecimal approvedAmount) {

		UUID userId = SecurityUtils.getCurrentUserId();
		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
			throw new BusinessException("Only claims under review can be approved");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		/*
		 * ClaimsOfficer can only approve their own assigned claims.
		 */
		if (user.getRole() == Role.CLAIMS_OFFICER && !userId.equals(claim.getAssignedOfficerId())) {

			throw new AccessDeniedException("Claim is not assigned to you");
		}

		/*
		 * Fraud flagged claim. ClaimsOfficer cannot approve it.
		 */
		if (claim.isFraudFlagged() && user.getRole() == Role.CLAIMS_OFFICER) {

			throw new AccessDeniedException("Fraud-flagged claim requires TenantAdmin approval");
		}

		/*
		 * ClaimsOfficer approval limit.
		 */
		if (user.getRole() == Role.CLAIMS_OFFICER && approvedAmount.compareTo(user.getApprovalLimit()) > 0) {

			claim.transitionTo(ClaimStatus.ESCALATED);

			claimRepository.save(claim);

			return maptoResponse(claim);
		}

		claim.setApprovedAmount(approvedAmount);

		claim.transitionTo(ClaimStatus.APPROVED);

		Claim savedClaim = claimRepository.save(claim);

		return maptoResponse(savedClaim);
	}

	@Transactional
	public ClaimResponse rejectClaim(UUID claimId, String reason) {

		UUID userId = SecurityUtils.getCurrentUserId();
		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		if (claim.getStatus() != ClaimStatus.UNDER_REVIEW && claim.getStatus() != ClaimStatus.ESCALATED
				&& claim.getStatus() != ClaimStatus.PENDING_DOCUMENTS) {

			throw new BusinessException("Claim cannot be rejected in its current state");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		/*
		 * ClaimsOfficer can reject only their assigned claim.
		 */
		if (user.getRole() == Role.CLAIMS_OFFICER && !userId.equals(claim.getAssignedOfficerId())) {

			throw new AccessDeniedException("Claim is not assigned to you");
		}

		claim.setRejectionReason(reason);

		claim.transitionTo(ClaimStatus.REJECTED);

		Claim savedClaim = claimRepository.save(claim);

		return maptoResponse(savedClaim);
	}

	@Transactional
	public ClaimResponse requestDocuments(UUID claimId, String reason) {

		UUID userId = SecurityUtils.getCurrentUserId();

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {

			throw new BusinessException("Documents can only be requested for claims under review");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (user.getRole() == Role.CLAIMS_OFFICER && !userId.equals(claim.getAssignedOfficerId())) {

			throw new AccessDeniedException("Claim is not assigned to you");
		}

		claim.setDocumentRequestReason(reason);

		claim.transitionTo(ClaimStatus.PENDING_DOCUMENTS);

		Claim savedClaim = claimRepository.save(claim);

		return maptoResponse(savedClaim);
	}

	@Transactional
	public ClaimResponse reviewDocuments(UUID claimId) {

		UUID userId = SecurityUtils.getCurrentUserId();

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		if (claim.getStatus() != ClaimStatus.PENDING_DOCUMENTS) {

			throw new BusinessException("Claim is not pending documents");
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (user.getRole() == Role.CLAIMS_OFFICER && !userId.equals(claim.getAssignedOfficerId())) {

			throw new AccessDeniedException("Claim is not assigned to you");
		}

		List<ClaimDocument> documents = claimDocumentRepository.findByClaimId(claimId);

		if (documents.isEmpty()) {
			throw new BusinessException("No supporting documents have been uploaded");
		}

		claim.transitionTo(ClaimStatus.UNDER_REVIEW);

		Claim savedClaim = claimRepository.save(claim);

		return maptoResponse(savedClaim);
	}

	@Transactional
	public ClaimResponse settleClaim(UUID claimId) {

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		/*
		 * Tenant isolation
		 */
		if (!claim.getTenantId().equals(tenantId)) {
			throw new ResourceNotFoundException("Claim not found");
		}

		/*
		 * Only APPROVED claims can be settled.
		 */
		if (claim.getStatus() != ClaimStatus.APPROVED) {
			throw new BusinessException("Only approved claims can be settled");
		}

		Policy policy = claim.getPolicy();

		/*
		 * Calculate remaining policy balance.
		 */
		BigDecimal remainingAmount = policy.getAnnualLimit().subtract(policy.getUsedAmount());

		/*
		 * Make sure the approved amount can still be covered.
		 */
		if (claim.getApprovedAmount().compareTo(remainingAmount) > 0) {

			throw new BusinessException("Insufficient policy balance");
		}

		/*
		 * Deduct approved amount.
		 */
		policy.setUsedAmount(policy.getUsedAmount().add(claim.getApprovedAmount()));

		/*
		 * APPROVED → SETTLED
		 */
		claim.transitionTo(ClaimStatus.SETTLED);

		/*
		 * Hibernate automatically increments
		 * 
		 * @Version on both entities.
		 */
		policyRepository.save(policy);
		claimRepository.save(claim);

		return maptoResponse(claim);
	}

	private ClaimResponse maptoResponse(Claim claim) {

		return new ClaimResponse(claim.getId(), claim.getPolicy().getId(), claim.getTreatmentDate(),
				claim.getIcd10Code(), claim.getProviderId(), claim.getClaimedAmount(), claim.getApprovedAmount(),
				claim.getStatus(), claim.getFraudScore(), claim.isFraudFlagged());
	}
}