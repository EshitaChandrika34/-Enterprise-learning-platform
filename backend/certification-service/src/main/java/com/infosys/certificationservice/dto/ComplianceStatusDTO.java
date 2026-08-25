package com.infosys.certificationservice.dto;

import java.util.UUID;

public class ComplianceStatusDTO {

    private UUID employeeId;
    private String complianceStatus;
    private long totalCertifications;
    private long activeCertifications;
    private long expiredCertifications;
    private long expiringSoon;
    private String message;

    public ComplianceStatusDTO() {
    }

    public ComplianceStatusDTO(
            UUID employeeId,
            String complianceStatus,
            long totalCertifications,
            long activeCertifications,
            long expiredCertifications,
            long expiringSoon,
            String message) {

        this.employeeId = employeeId;
        this.complianceStatus = complianceStatus;
        this.totalCertifications = totalCertifications;
        this.activeCertifications = activeCertifications;
        this.expiredCertifications = expiredCertifications;
        this.expiringSoon = expiringSoon;
        this.message = message;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public long getTotalCertifications() {
        return totalCertifications;
    }

    public void setTotalCertifications(long totalCertifications) {
        this.totalCertifications = totalCertifications;
    }

    public long getActiveCertifications() {
        return activeCertifications;
    }

    public void setActiveCertifications(long activeCertifications) {
        this.activeCertifications = activeCertifications;
    }

    public long getExpiredCertifications() {
        return expiredCertifications;
    }

    public void setExpiredCertifications(long expiredCertifications) {
        this.expiredCertifications = expiredCertifications;
    }

    public long getExpiringSoon() {
        return expiringSoon;
    }

    public void setExpiringSoon(long expiringSoon) {
        this.expiringSoon = expiringSoon;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}