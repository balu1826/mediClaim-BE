package com.mediclaim.mediclaim.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_email_tenant",
            columnNames = {"tenant_id", "email"}
        )
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private boolean fraudSpecialist;
	@Column(precision = 18, scale = 2)
	private BigDecimal approvalLimit;

    private boolean onLeave;

    private LocalDateTime lastAssignedAt;

    public User() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

	public boolean isFraudSpecialist() {
		return fraudSpecialist;
	}

	public void setFraudSpecialist(boolean fraudSpecialist) {
		this.fraudSpecialist = fraudSpecialist;
	}

	public boolean isOnLeave() {
		return onLeave;
	}

	public void setOnLeave(boolean onLeave) {
		this.onLeave = onLeave;
	}

	public LocalDateTime getLastAssignedAt() {
		return lastAssignedAt;
	}

	public void setLastAssignedAt(LocalDateTime lastAssignedAt) {
		this.lastAssignedAt = lastAssignedAt;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public BigDecimal getApprovalLimit() {
		return approvalLimit;
	}

	public void setApprovalLimit(BigDecimal approvalLimit) {
		this.approvalLimit = approvalLimit;
	}
	
    
}