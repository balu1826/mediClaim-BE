package com.mediclaim.mediclaim.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {

        Jwt jwt = getJwt();
        return UUID.fromString(jwt.getSubject());
    }

    public static UUID getCurrentTenantId() {

        Jwt jwt = getJwt();
        return UUID.fromString(jwt.getClaimAsString("tenantId"));
    }

    public static String getCurrentRole() {
    	
        Jwt jwt = getJwt();
        return jwt.getClaimAsString("role");
    }

    private static Jwt getJwt() {

        Authentication authentication =SecurityContextHolder
							                .getContext()
							                .getAuthentication();
        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException( "Authenticated JWT not found" ); 
        }

        return jwt;
    }
}