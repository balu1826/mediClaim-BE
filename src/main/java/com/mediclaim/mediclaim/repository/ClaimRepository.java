package com.mediclaim.mediclaim.repository;

import com.mediclaim.mediclaim.entity.Claim;
import com.mediclaim.mediclaim.entity.ClaimStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {

	List<Claim> findByTenantIdAndPatientId(UUID tenantId, UUID patientId);

	long countByPatientIdAndTreatmentDateAfter(UUID patientId, LocalDate date);

	boolean existsByPatientIdAndProviderIdAndIcd10CodeAndTreatmentDate(UUID patientId, UUID providerId,
			String icd10Code, LocalDate treatmentDate);

	List<Claim> findByPatientIdAndIcd10CodeAndStatusAndCreatedAtAfter(UUID patientId, String icd10Code,
			ClaimStatus status, LocalDateTime dateTime);

	@Query("""
			    SELECT COUNT(c)
			    FROM Claim c
			    WHERE c.patientId = :patientId
			      AND c.icd10Code = :icd10Code
			      AND c.treatmentDate >= :fromDate
			      AND c.treatmentDate <= :toDate
			      AND c.status <> :draftStatus
			""")
	long countClaimsByPatientAndIcd10(@Param("patientId") UUID patientId, @Param("icd10Code") String icd10Code,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

	@Query(value = """
			SELECT PERCENTILE_CONT(0.5)
			WITHIN GROUP (ORDER BY c.approved_amount)
			FROM claims c
			WHERE c.tenant_id = :tenantId
			  AND c.icd10_code = :icd10Code
			  AND c.approved_amount IS NOT NULL
			""", nativeQuery = true)
	BigDecimal findMedianApprovedAmount(@Param("tenantId") UUID tenantId, @Param("icd10Code") String icd10Code);

	@Query("""
			    SELECT COUNT(c)
			    FROM Claim c
			    WHERE c.patientId = :patientId
			      AND c.icd10Code = :icd10Code
			      AND c.status = :rejectedStatus
			      AND c.treatmentDate >= :fromDate
			      AND c.treatmentDate < :currentDate
			""")
	long countPreviousRejectedClaims(@Param("patientId") UUID patientId, @Param("icd10Code") String icd10Code,
			@Param("rejectedStatus") ClaimStatus rejectedStatus, @Param("fromDate") LocalDate fromDate,
			@Param("currentDate") LocalDate currentDate);

	@Query("""
			    SELECT COUNT(c)
			    FROM Claim c
			    WHERE c.assignedOfficerId = :officerId
			      AND c.status IN :statuses
			""")
	long countOpenClaims(@Param("officerId") UUID officerId, @Param("statuses") Collection<ClaimStatus> statuses);

}
