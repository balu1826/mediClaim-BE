package com.mediclaim.mediclaim.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimStatus;
import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.entity.UserStatus;
import com.mediclaim.mediclaim.repository.ClaimRepository;
import com.mediclaim.mediclaim.repository.UserRepository;

@Service
public class ClaimAssignmentService {

	private final UserRepository userRepository;
	private final ClaimRepository claimRepository;

	public ClaimAssignmentService(UserRepository userRepository, ClaimRepository claimRepository) {

		this.userRepository = userRepository;
		this.claimRepository = claimRepository;
	}

	@Transactional
	public Claim assignClaim(Claim claim) {

		UUID tenantId = claim.getTenantId();

		List<User> officers = userRepository.findByTenantIdAndRoleAndStatus(tenantId, Role.CLAIMS_OFFICER,
				UserStatus.ACTIVE);

		officers = officers.stream().filter(officer -> !officer.isOnLeave()).toList();

		/*
		 * Fraud claim: only fraud specialists are eligible.
		 */
		if (claim.isFraudFlagged()) {

			officers = officers.stream().filter(User::isFraudSpecialist).toList();
		}

		if (officers.isEmpty()) {

			claim.setStatus(ClaimStatus.PENDING_ASSIGNMENT);

			return claimRepository.save(claim);

		
		}

		User selectedOfficer = findBestOfficer(officers);

		claim.setAssignedOfficerId(selectedOfficer.getId());

		claim.setStatus(ClaimStatus.UNDER_REVIEW);

		Claim savedClaim=claimRepository.save(claim);

		selectedOfficer.setLastAssignedAt(LocalDateTime.now());

		userRepository.save(selectedOfficer);
		return savedClaim;
	}

	private User findBestOfficer(List<User> officers) {

		return officers.stream().min(Comparator.comparingLong(this::getOpenClaimCount)
				.thenComparing(User::getLastAssignedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
				.orElseThrow();
	}

	private long getOpenClaimCount(User officer) {

		return claimRepository.countOpenClaims(officer.getId(),
				List.of(ClaimStatus.UNDER_REVIEW, ClaimStatus.PENDING_DOCUMENTS));
	}
}