package com.infosys.certificationservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class CertificationRenewalDTO {

    @NotNull(message = "New expiry date is required")
    private LocalDate newExpiryDate;

    public CertificationRenewalDTO() {
    }

    public LocalDate getNewExpiryDate() {
        return newExpiryDate;
    }

    public void setNewExpiryDate(LocalDate newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }
}