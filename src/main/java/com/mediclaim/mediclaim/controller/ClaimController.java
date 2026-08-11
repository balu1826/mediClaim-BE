package com.mediclaim.mediclaim.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mediclaim.mediclaim.dto.claim.ClaimResponse;
import com.mediclaim.mediclaim.dto.claim.ClaimTransitionRequest;
import com.mediclaim.mediclaim.dto.claim.CreateClaimDraftRequest;
import com.mediclaim.mediclaim.dto.claim.PendingDocumentsRequest;
import com.mediclaim.mediclaim.dto.claim.RejectClaimRequest;
import com.mediclaim.mediclaim.service.ClaimDocumentService;
import com.mediclaim.mediclaim.service.ClaimService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

	private final ClaimService claimService;
	private final ClaimDocumentService claimDocumentService;

	public ClaimController(ClaimService claimService, ClaimDocumentService claimDocumentService) {

		this.claimService = claimService;
		this.claimDocumentService = claimDocumentService;

	}

	@PostMapping("/draft")
	@PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<ClaimResponse> createDraft(@Valid @RequestBody CreateClaimDraftRequest request) {

		ClaimResponse response = claimService.createDraft(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{claimId}/submit")
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<ClaimResponse> submitClaim(@PathVariable UUID claimId) {

		ClaimResponse response = claimService.submitClaim(claimId);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{claimId}/transition")
	@PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'TENANT_ADMIN')")
	public ResponseEntity<ClaimResponse> transitionClaim(@PathVariable UUID claimId,
			@Valid @RequestBody ClaimTransitionRequest request) {

		return ResponseEntity.ok(claimService.transitionClaim(claimId, request.getStatus()));
	}

	@PostMapping("/{claimId}/reject")
	@PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'TENANT_ADMIN')")
	public ResponseEntity<ClaimResponse> rejectClaim(@PathVariable UUID claimId,
			@Valid @RequestBody RejectClaimRequest request) {

		ClaimResponse response = claimService.rejectClaim(claimId, request.getReason());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{claimId}/request-documents")
	@PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'TENANT_ADMIN')")
	public ResponseEntity<ClaimResponse> requestDocuments(@PathVariable UUID claimId,
			@Valid @RequestBody PendingDocumentsRequest request) {

		ClaimResponse response = claimService.requestDocuments(claimId, request.getReason());

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/{claimId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('CLIENT')")
	public ResponseEntity<Void> uploadDocument(@PathVariable UUID claimId, @RequestParam("file") MultipartFile file) {

		claimDocumentService.uploadDocument(claimId, file);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/{claimId}/documents/review")
	@PreAuthorize("hasAnyRole('CLAIMS_OFFICER', 'TENANT_ADMIN')")
	public ResponseEntity<ClaimResponse> reviewDocuments(@PathVariable UUID claimId) {

		ClaimResponse response = claimService.reviewDocuments(claimId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{claimId}/settle")
	@PreAuthorize("hasRole('TENANT_ADMIN')")
	public ResponseEntity<ClaimResponse> settleClaim(@PathVariable UUID claimId) {

		ClaimResponse response = claimService.settleClaim(claimId);

		return ResponseEntity.ok(response);
	}
}