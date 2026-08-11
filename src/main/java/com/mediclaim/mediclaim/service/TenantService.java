package com.mediclaim.mediclaim.service;

import com.mediclaim.mediclaim.dto.tenant.CreateTenantRequest;
import com.mediclaim.mediclaim.dto.tenant.CreateTenantResponse;
import com.mediclaim.mediclaim.dto.tenant.TenantResponse;
import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.entity.TenantStatus;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.repository.TenantRepository;
import com.mediclaim.mediclaim.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public TenantService(TenantRepository tenantRepository,UserRepository userRepository,
    		PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository=userRepository;
		this.passwordEncoder =passwordEncoder;
    }

    @Transactional
    public CreateTenantResponse createTenant(CreateTenantRequest request) {

        if (tenantRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Tenant code already exists", HttpStatus.CONFLICT);
        }

        if (tenantRepository.existsByName(request.getName())) {
            throw new BusinessException("Tenant name already exists", HttpStatus.CONFLICT);  
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User with t already exists", HttpStatus.CONFLICT);  
        }

        Tenant tenant = new Tenant(
                request.getName(),
                request.getCode(),
                TenantStatus.ACTIVE
        );

        Tenant savedTenant = tenantRepository.save(tenant);
        User  user=new User();
        user.setEmail(request.getEmail());
        user.setName(request.getAdminName());
        user.setTenant(savedTenant);
        user.setRole(Role.TENANT_ADMIN);
        user.setPassword(passwordEncoder.encode( request.getPassword()));
        
        User savedUser=userRepository.save(user);

        return mapToResponse(savedTenant,savedUser);
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
    private CreateTenantResponse mapToResponse(Tenant tenant,User user) {

        return new CreateTenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getCode(),
                tenant.getStatus(),
                tenant.getCreatedAt(),
                user.getName(),
                user.getEmail()
        );
    }
}