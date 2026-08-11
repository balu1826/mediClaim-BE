package com.mediclaim.mediclaim.repository;

import com.mediclaim.mediclaim.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

	boolean existsByTenantIdAndCode(UUID tenantId, String code);
	Optional<Policy> findByCode(String code);
}