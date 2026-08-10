package com.mediclaim.mediclaim.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediclaim.mediclaim.entity.User;

public interface UserRepository  extends JpaRepository<User, UUID>{
	boolean existsByEmailAndTenantId(String email,UUID tenantId);
}
