package com.mediclaim.mediclaim.dto.tenant;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mediclaim.mediclaim.entity.TenantStatus;

public class CreateTenantResponse {

	  private UUID id;
	    private String name;
	    private String code;
	    private TenantStatus status;
	    private LocalDateTime createdAt;
	    private String adminName;
	    private String eMail;

	    public CreateTenantResponse(
	            UUID id,
	            String name,
	            String code,
	            TenantStatus status,
	            LocalDateTime createdAt,
	            String adminName,
	            String eMail) {

	        this.id = id;
	        this.name = name;
	        this.code = code;
	        this.status = status;
	        this.createdAt = createdAt;
	        this.adminName=adminName;
	        this.eMail=eMail;
	    }

	    public UUID getId() {
	        return id;
	    }

	    public String getName() {
	        return name;
	    }

	    public String getCode() {
	        return code;
	    }

	    public TenantStatus getStatus() {
	        return status;
	    }

	    public LocalDateTime getCreatedAt() {
	        return createdAt;
	    }
	    public String getAdminName() {
	        return adminName;
	    }

	    public String getEMail() {
	        return eMail;
	    }
}
