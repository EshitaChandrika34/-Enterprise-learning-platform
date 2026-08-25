package com.infosys.certificationservice.dto;

public class CertificationReportDTO {

    private long totalCertifications;
    private long activeCertifications;
    private long expiredCertifications;
    private long expiringSoon;
    private long renewedCertifications;
    private double renewalRate;

    public CertificationReportDTO() {
    }

    public CertificationReportDTO(
            long totalCertifications,
            long activeCertifications,
            long expiredCertifications,
            long expiringSoon,
            long renewedCertifications,
            double renewalRate) {

        this.totalCertifications = totalCertifications;
        this.activeCertifications = activeCertifications;
        this.expiredCertifications = expiredCertifications;
        this.expiringSoon = expiringSoon;
        this.renewedCertifications = renewedCertifications;
        this.renewalRate = renewalRate;
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

    public long getRenewedCertifications() {
        return renewedCertifications;
    }

    public void setRenewedCertifications(long renewedCertifications) {
        this.renewedCertifications = renewedCertifications;
    }

    public double getRenewalRate() {
        return renewalRate;
    }

    public void setRenewalRate(double renewalRate) {
        this.renewalRate = renewalRate;
    }
}