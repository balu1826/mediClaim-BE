package com.mediclaim.mediclaim.repository;

import com.mediclaim.mediclaim.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

	List<Policy> findByTenantIdAndPatientId(UUID tenantId, UUID patientId);

	boolean existsByTenantIdAndPatientIdAndPolicyTypeId(UUID tenantId, UUID patientId, UUID policyTypeId);

	long countByTenantId(UUID tenantId);

	Optional<Policy> findByTenantIdAndPolicyNumber(UUID tenantId, String policyNumber);
}