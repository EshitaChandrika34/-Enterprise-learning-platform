package com.infosys.skillmicroservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.skillmicroservice.entity.Certification;
import com.infosys.skillmicroservice.repository.CertificationRepository;
import com.infosys.skillmicroservice.service.CertificationService;

@Service
public class CertificationServiceImpl
        implements CertificationService {

    private final CertificationRepository certificationRepository;

    public CertificationServiceImpl(
            CertificationRepository certificationRepository) {

        this.certificationRepository = certificationRepository;
    }

    @Override
    public Certification addCertification(
            Certification certification) {

        updateStatus(certification);

        return certificationRepository.save(certification);
    }

    @Override
    public List<Certification> getAllCertifications() {

        List<Certification> certifications =
                certificationRepository.findAll();

        certifications.forEach(this::updateStatus);

        return certifications;
    }

    @Override
    public Certification getCertificationById(
            UUID certificationId) {

        Certification certification =
                certificationRepository.findById(certificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Certification not found with ID: "
                                                + certificationId));

        updateStatus(certification);

        return certification;
    }

    @Override
    public List<Certification> getCertificationsByUserId(
            UUID userId) {

        List<Certification> certifications =
                certificationRepository.findByUserId(userId);

        certifications.forEach(this::updateStatus);

        return certifications;
    }

    @Override
    public Certification updateCertification(
            UUID certificationId,
            Certification newCertification) {

        Certification existingCertification =
                getCertificationById(certificationId);

        existingCertification.setUserId(
                newCertification.getUserId());

        existingCertification.setCertificationName(
                newCertification.getCertificationName());

        existingCertification.setIssueDate(
                newCertification.getIssueDate());

        existingCertification.setExpiryDate(
                newCertification.getExpiryDate());

        updateStatus(existingCertification);

        return certificationRepository.save(existingCertification);
    }

    @Override
    public void deleteCertification(UUID certificationId) {

        Certification certification =
                getCertificationById(certificationId);

        certificationRepository.delete(certification);
    }

    private void updateStatus(Certification certification) {

        if (certification.getExpiryDate() == null) {
            certification.setStatus("UNKNOWN");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = certification.getExpiryDate();

        if (expiryDate.isBefore(today)) {
            certification.setStatus("EXPIRED");
        } else if (!expiryDate.isAfter(today.plusDays(30))) {
            certification.setStatus("EXPIRING_SOON");
        } else {
            certification.setStatus("VALID");
        }
    }
}