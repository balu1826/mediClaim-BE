package com.mediclaim.mediclaim.repository;

import com.mediclaim.mediclaim.entity.PolicyType;
import com.mediclaim.mediclaim.entity.PolicyTypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyTypeRepository extends JpaRepository<PolicyType, UUID> {

	List<PolicyType> findByTenantIdAndStatus(UUID tenantId, PolicyTypeStatus status);

	boolean existsByTenantIdAndName(UUID tenantId, String name);
}