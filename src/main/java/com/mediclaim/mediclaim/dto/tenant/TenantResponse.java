package com.mediclaim.mediclaim.dto.tenant;

import com.mediclaim.mediclaim.entity.TenantStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TenantResponse {

    private UUID id;
    private String name;
    private String code;
    private TenantStatus status;
    private LocalDateTime createdAt;

    public TenantResponse(
            UUID id,
            String name,
            String code,
            TenantStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
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
}