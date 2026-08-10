package com.mediclaim.mediclaim.service;

import com.mediclaim.mediclaim.dto.user.CreatePatientRequest;
import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.entity.TenantStatus;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.entity.UserStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.repository.TenantRepository;
import com.mediclaim.mediclaim.repository.UserRepository;
import com.mediclaim.mediclaim.security.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            TenantRepository tenantRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public void createPatient(CreatePatientRequest request) {

        UUID tenantId =SecurityUtils.getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() ->new BusinessException(   "Tenant not found" ));
        if (tenant.getStatus() !=TenantStatus.ACTIVE) {
            throw new BusinessException("Tenant is not active");
        }
        if (userRepository.existsByEmailAndTenantId(request.getEmail(),  tenantId)) {
            throw new BusinessException("User with this email already exists");     
        }

        User user = new User();
        user.setTenant(tenant);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode( request.getPassword()));
               
        // Role is controlled by the server.
        user.setRole(Role.PATIENT);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }
}