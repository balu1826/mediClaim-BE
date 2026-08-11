package com.mediclaim.mediclaim.config;

import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.Tenant;
import com.mediclaim.mediclaim.entity.TenantStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.entity.UserStatus;
import com.mediclaim.mediclaim.repository.TenantRepository;
import com.mediclaim.mediclaim.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log =LoggerFactory.getLogger(SuperAdminBootstrap.class);

    @Value("${bootstrap.admin.email}")
    private String adminEmail;

    @Value("${bootstrap.admin.password}")
    private String adminPassword;

    public SuperAdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TenantRepository tenantRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantRepository=tenantRepository;
    }

    @Override
    public void run(String... args) {

        if (userRepository.existsByRole(Role.SUPER_ADMIN)) {
            return;
        }

        User superAdmin = new User();

        superAdmin.setName("System Administrator");
        superAdmin.setEmail(adminEmail);
        superAdmin.setPassword(passwordEncoder.encode(adminPassword));
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setStatus(UserStatus.ACTIVE);
        superAdmin.setTenant(getOrCreateMediClaimTenant());

        userRepository.save(superAdmin);
        log.info("Initial SUPER_ADMIN created successfully" );
           
    }
    private Tenant getOrCreateMediClaimTenant() {

        return tenantRepository.findByCode("MEDICLAIM")
                .orElseGet(() -> {

                    Tenant tenant = new Tenant();

                    tenant.setName("MediClaim");
                    tenant.setCode("MEDICLAIM");
                    tenant.setStatus(TenantStatus.ACTIVE);

                    Tenant saved = tenantRepository.save(tenant);

                    log.info(
                            "MediClaim tenant created: tenantId={}",
                            saved.getId()
                    );

                    return saved;
                });
    }
}