package com.mediclaim.mediclaim.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediclaim.mediclaim.entity.ClaimDocument;

public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, UUID> {

	List<ClaimDocument> findByClaimId(UUID claimId);
}