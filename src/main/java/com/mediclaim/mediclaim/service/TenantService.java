package com.mediclaim.mediclaim.service;

import com.mediclaim.mediclaim.dto.tenant.CreateTenantRequest;
import com.mediclaim.mediclaim.dto.tenant.TenantResponse;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.entity.TenantStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public TenantResponse createTenant(CreateTenantRequest request) {

        if (tenantRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Tenant code already exists");
        }

        if (tenantRepository.existsByName(request.getName())) {
            throw new BusinessException("Tenant name already exists");  
        }

        Tenant tenant = new Tenant(
                request.getName(),
                request.getCode(),
                TenantStatus.ACTIVE
        );

        Tenant savedTenant = tenantRepository.save(tenant);

        return mapToResponse(savedTenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> getAllTenants() {

        return tenantRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TenantResponse mapToResponse(Tenant tenant) {

        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getCode(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }
}