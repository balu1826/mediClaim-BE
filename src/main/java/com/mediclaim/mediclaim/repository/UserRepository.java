package com.mediclaim.mediclaim.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediclaim.mediclaim.entity.Role;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.entity.UserStatus;

public interface UserRepository  extends JpaRepository<User, UUID>{
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	boolean existsByEmailAndTenantId(String email,UUID tenantId);
	Optional<User> findByEmailAndTenantId(String email,UUID tenantId); 
	boolean existsByRole(Role role);
	List<User> findByTenantIdAndRoleAndStatus(UUID tenantId, Role role, UserStatus status);
}
