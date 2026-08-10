package com.mediclaim.mediclaim.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    @Size(max = 100, message = "Tenant name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Tenant code is required")
    @Size(max = 50, message = "Tenant code cannot exceed 50 characters")
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}