package com.infosys.certificationservice.dto;

import java.time.LocalDate;
import java.util.UUID;

public class RenewalNotificationDTO {

    private UUID certificationId;
    private UUID employeeId;
    private String certificationName;
    private LocalDate expiryDate;
    private long daysRemaining;
    private String message;

    public RenewalNotificationDTO() {
    }

    public RenewalNotificationDTO(
            UUID certificationId,
            UUID employeeId,
            String certificationName,
            LocalDate expiryDate,
            long daysRemaining,
            String message) {

        this.certificationId = certificationId;
        this.employeeId = employeeId;
        this.certificationName = certificationName;
        this.expiryDate = expiryDate;
        this.daysRemaining = daysRemaining;
        this.message = message;
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

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}