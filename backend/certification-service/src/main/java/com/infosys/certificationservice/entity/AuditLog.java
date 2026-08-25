package com.infosys.certificationservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "certification_audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private UUID certificationId;

    private UUID employeeId;

    private String action;

    private String description;

    private LocalDateTime actionTime;

    @PrePersist
    public void beforeInsert() {
        actionTime = LocalDateTime.now();
    }

    public AuditLog() {
    }

    public AuditLog(
            UUID certificationId,
            UUID employeeId,
            String action,
            String description) {

        this.certificationId = certificationId;
        this.employeeId = employeeId;
        this.action = action;
        this.description = description;
    }

    public Long getAuditId() {
        return auditId;
    }

    public UUID getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(UUID certificationId) {
        this.certificationId = certificationId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }
}