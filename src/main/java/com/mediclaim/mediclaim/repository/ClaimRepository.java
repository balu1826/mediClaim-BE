package com.mediclaim.mediclaim.repository;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

	List<Claim> findByTenantIdAndPatientId(UUID tenantId, UUID patientId);

	long countByPatientIdAndTreatmentDateAfter(UUID patientId, LocalDate date);

	boolean existsByPatientIdAndProviderIdAndIcd10CodeAndTreatmentDate(UUID patientId, UUID providerId,
			String icd10Code, LocalDate treatmentDate);

	List<Claim> findByPatientIdAndIcd10CodeAndStatusAndCreatedAtAfter(UUID patientId, String icd10Code,
			ClaimStatus status, LocalDateTime dateTime);
}