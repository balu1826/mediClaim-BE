package com.mediclaim.mediclaim.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimDocument;
import com.mediclaim.mediclaim.entity.ClaimStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.exception.ResourceNotFoundException;
import com.mediclaim.mediclaim.repository.ClaimDocumentRepository;
import com.mediclaim.mediclaim.repository.ClaimRepository;
import com.mediclaim.mediclaim.security.SecurityUtils;

@Service
public class ClaimDocumentService {

	private final ClaimRepository claimRepository;
	private final ClaimDocumentRepository documentRepository;

	@Value("${app.upload.claim-documents}")
	private String uploadDirectory;

	public ClaimDocumentService(ClaimRepository claimRepository, ClaimDocumentRepository documentRepository) {

		this.claimRepository = claimRepository;
		this.documentRepository = documentRepository;
	}

	@Transactional
	public void uploadDocument(UUID claimId, MultipartFile file) {

		UUID userId = SecurityUtils.getCurrentUserId();

		UUID tenantId = SecurityUtils.getCurrentTenantId();

		Claim claim = claimRepository.findById(claimId)
				.orElseThrow(() -> new ResourceNotFoundException("Claim not found"));

		if (!claim.getTenantId().equals(tenantId) || !claim.getPatientId().equals(userId)) {

			throw new ResourceNotFoundException("Claim not found");
		}

		if (claim.getStatus() != ClaimStatus.PENDING_DOCUMENTS) {

			throw new BusinessException("Documents can only be uploaded for claims pending documents");
		}

		if (file.isEmpty()) {
			throw new BusinessException("File cannot be empty");
		}

		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

		Path directory = Paths.get(uploadDirectory);

		Path filePath = directory.resolve(fileName);

		try {

			Files.createDirectories(directory);

			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException ex) {

			throw new BusinessException("Unable to store document");
		}

		ClaimDocument document = new ClaimDocument();

		document.setClaim(claim);
		document.setFileName(file.getOriginalFilename());
		document.setContentType(file.getContentType());
		document.setFileSize(file.getSize());
		document.setFilePath(filePath.toString());
		document.setUploadedAt(LocalDateTime.now());

		documentRepository.save(document);
	}
}