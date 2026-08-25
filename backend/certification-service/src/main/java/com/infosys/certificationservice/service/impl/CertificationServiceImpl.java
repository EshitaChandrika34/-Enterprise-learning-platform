package com.infosys.certificationservice.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.certificationservice.dto.CertificationRenewalDTO;
import com.infosys.certificationservice.dto.CertificationReportDTO;
import com.infosys.certificationservice.dto.CertificationRequestDTO;
import com.infosys.certificationservice.dto.ComplianceStatusDTO;
import com.infosys.certificationservice.dto.RenewalNotificationDTO;
import com.infosys.certificationservice.entity.Certification;
import com.infosys.certificationservice.exception.CertificationNotFoundException;
import com.infosys.certificationservice.repository.CertificationRepository;
import com.infosys.certificationservice.service.AuditLogService;
import com.infosys.certificationservice.service.CertificationService;

@Service
public class CertificationServiceImpl
        implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final AuditLogService auditLogService;

    public CertificationServiceImpl(
            CertificationRepository certificationRepository,
            AuditLogService auditLogService) {

        this.certificationRepository = certificationRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public Certification addCertification(
            CertificationRequestDTO request) {

        validateDates(
                request.getIssueDate(),
                request.getExpiryDate()
        );

        if (request.getCertificateNumber() != null
                && !request.getCertificateNumber().isBlank()
                && certificationRepository
                .existsByCertificateNumber(
                        request.getCertificateNumber())) {

            throw new IllegalArgumentException(
                    "Certificate number already exists"
            );
        }

        Certification certification = new Certification();

        copyRequestToEntity(request, certification);

        certification.setRenewed(false);
        certification.setStatus("ACTIVE");
        
        Certification savedCertification =
                certificationRepository.save(certification);

        auditLogService.createAuditLog(
                savedCertification.getCertificationId(),
                savedCertification.getEmployeeId(),
                "CERTIFICATION_CREATED",
                "Certification created successfully"
        );

        return savedCertification;
    }

    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    public Certification getCertificationById(UUID id) {

        return certificationRepository.findById(id)
                .orElseThrow(() ->
                        new CertificationNotFoundException(
                                "Certification not found with ID: " + id
                        )
                );
    }

    @Override
    public List<Certification> getCertificationsByEmployee(
            UUID employeeId) {

        return certificationRepository
                .findByEmployeeId(employeeId);
    }

    @Override
    public Certification updateCertification(
            UUID id,
            CertificationRequestDTO request) {

        validateDates(
                request.getIssueDate(),
                request.getExpiryDate()
        );

        Certification certification =
                getCertificationById(id);

        if (request.getCertificateNumber() != null
                && !request.getCertificateNumber().isBlank()
                && !request.getCertificateNumber().equals(
                        certification.getCertificateNumber())
                && certificationRepository
                .existsByCertificateNumber(
                        request.getCertificateNumber())) {

            throw new IllegalArgumentException(
                    "Certificate number already exists"
            );
        }

        copyRequestToEntity(request, certification);

        Certification updatedCertification =
                certificationRepository.save(certification);

        auditLogService.createAuditLog(
                updatedCertification.getCertificationId(),
                updatedCertification.getEmployeeId(),
                "CERTIFICATION_UPDATED",
                "Certification details updated"
        );

        return updatedCertification;
    }

    @Override
    public void deleteCertification(UUID id) {

        Certification certification =
                getCertificationById(id);

        auditLogService.createAuditLog(
                certification.getCertificationId(),
                certification.getEmployeeId(),
                "CERTIFICATION_DELETED",
                "Certification deleted"
        );

        certificationRepository.delete(certification);
    }

    @Override
    public List<Certification> getActiveCertifications() {

        return certificationRepository
                .findByExpiryDateGreaterThanEqual(
                        LocalDate.now()
                );
    }

    @Override
    public List<Certification> getExpiredCertifications() {

        return certificationRepository
                .findByExpiryDateBefore(LocalDate.now());
    }

    @Override
    public List<Certification> getExpiringCertifications(
            int days) {

        if (days < 0) {
            throw new IllegalArgumentException(
                    "Days must be zero or greater"
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return certificationRepository
                .findByExpiryDateBetween(today, endDate);
    }

    @Override
    public Certification renewCertification(
            UUID id,
            CertificationRenewalDTO request) {

        Certification certification =
                getCertificationById(id);

        LocalDate newExpiryDate =
                request.getNewExpiryDate();

        if (!newExpiryDate.isAfter(
                certification.getExpiryDate())) {

            throw new IllegalArgumentException(
                    "New expiry date must be after the current expiry date"
            );
        }

        if (!newExpiryDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "New expiry date must be after today"
            );
        }

        certification.setExpiryDate(newExpiryDate);
        certification.setRenewed(true);

        Certification renewedCertification =
                certificationRepository.save(certification);

        auditLogService.createAuditLog(
                renewedCertification.getCertificationId(),
                renewedCertification.getEmployeeId(),
                "CERTIFICATION_RENEWED",
                "Certification renewed until "
                        + newExpiryDate
        );

        return renewedCertification;
    }

    @Override
    public List<RenewalNotificationDTO>
    getRenewalNotifications(int days) {

        List<Certification> certifications =
                getExpiringCertifications(days);

        LocalDate today = LocalDate.now();

        return certifications.stream()
                .map(certification -> {

                    long daysRemaining =
                            ChronoUnit.DAYS.between(
                                    today,
                                    certification.getExpiryDate()
                            );

                    String message =
                            certification.getCertificationName()
                                    + " will expire in "
                                    + daysRemaining
                                    + " days. Please renew it.";

                    return new RenewalNotificationDTO(
                            certification.getCertificationId(),
                            certification.getEmployeeId(),
                            certification.getCertificationName(),
                            certification.getExpiryDate(),
                            daysRemaining,
                            message
                    );
                })
                .toList();
    }

    @Override
    public CertificationReportDTO generateReport() {

        List<Certification> allCertifications =
                certificationRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDate nextThirtyDays =
                today.plusDays(30);

        long total = allCertifications.size();

        long active = allCertifications.stream()
                .filter(certification ->
                        !certification.getExpiryDate()
                                .isBefore(today))
                .count();

        long expired = allCertifications.stream()
                .filter(certification ->
                        certification.getExpiryDate()
                                .isBefore(today))
                .count();

        long expiringSoon = allCertifications.stream()
                .filter(certification ->
                        !certification.getExpiryDate()
                                .isBefore(today)
                                && !certification.getExpiryDate()
                                .isAfter(nextThirtyDays))
                .count();

        long renewed = allCertifications.stream()
                .filter(Certification::isRenewed)
                .count();

        double renewalRate = 0.0;

        if (total > 0) {
            renewalRate =
                    ((double) renewed / total) * 100;
        }

        return new CertificationReportDTO(
                total,
                active,
                expired,
                expiringSoon,
                renewed,
                renewalRate
        );
    }

    @Override
    public ComplianceStatusDTO getComplianceStatus(
            UUID employeeId) {

        List<Certification> certifications =
                certificationRepository
                        .findByEmployeeId(employeeId);

        LocalDate today = LocalDate.now();
        LocalDate nextThirtyDays =
                today.plusDays(30);

        long total = certifications.size();

        long active = certifications.stream()
                .filter(certification ->
                        !certification.getExpiryDate()
                                .isBefore(today))
                .count();

        long expired = certifications.stream()
                .filter(certification ->
                        certification.getExpiryDate()
                                .isBefore(today))
                .count();

        long expiringSoon = certifications.stream()
                .filter(certification ->
                        !certification.getExpiryDate()
                                .isBefore(today)
                                && !certification.getExpiryDate()
                                .isAfter(nextThirtyDays))
                .count();

        String status;
        String message;

        if (total == 0 || active == 0) {

            status = "NON_COMPLIANT";
            message =
                    "Employee has no active certifications.";

        } else if (expiringSoon > 0) {

            status = "EXPIRING_SOON";
            message =
                    "Employee has certifications expiring soon.";

        } else if (expired > 0) {

            status = "NON_COMPLIANT";
            message =
                    "Employee has expired certifications.";

        } else {

            status = "COMPLIANT";
            message =
                    "Employee certifications are valid.";
        }

        return new ComplianceStatusDTO(
                employeeId,
                status,
                total,
                active,
                expired,
                expiringSoon,
                message
        );
    }

    private void validateDates(
            LocalDate issueDate,
            LocalDate expiryDate) {

        if (issueDate == null || expiryDate == null) {
            throw new IllegalArgumentException(
                    "Issue date and expiry date are required"
            );
        }

        if (!expiryDate.isAfter(issueDate)) {
            throw new IllegalArgumentException(
                    "Expiry date must be after issue date"
            );
        }
    }

    private void copyRequestToEntity(
            CertificationRequestDTO request,
            Certification certification) {

        certification.setEmployeeId(
                request.getEmployeeId()
        );

        certification.setCertificationName(
                request.getCertificationName()
        );

        certification.setIssuingOrganization(
                request.getIssuingOrganization()
        );

        certification.setCertificateNumber(
                request.getCertificateNumber()
        );

        certification.setIssueDate(
                request.getIssueDate()
        );

        certification.setExpiryDate(
                request.getExpiryDate()
        );

        certification.setCertificateUrl(
                request.getCertificateUrl()
        );
    }
}