package com.infosys.learningservice.service;

import java.util.List;

import com.infosys.learningservice.dto.CertificateDTO;

public interface CertificateService {

    CertificateDTO generateCertificate(
            CertificateDTO certificateDTO);

    List<CertificateDTO> getAllCertificates();

    CertificateDTO getCertificateById(
            Long id);

    List<CertificateDTO>
    getCertificatesByEnrollmentId(
            Long enrollmentId);

    CertificateDTO updateCertificate(
            Long id,
            CertificateDTO certificateDTO);

    void deleteCertificate(
            Long id);

    boolean isEligibleForCertificate(
            Long enrollmentId);
}