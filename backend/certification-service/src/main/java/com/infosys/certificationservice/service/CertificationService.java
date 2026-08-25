package com.infosys.certificationservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.certificationservice.dto.CertificationRenewalDTO;
import com.infosys.certificationservice.dto.CertificationReportDTO;
import com.infosys.certificationservice.dto.CertificationRequestDTO;
import com.infosys.certificationservice.dto.ComplianceStatusDTO;
import com.infosys.certificationservice.dto.RenewalNotificationDTO;
import com.infosys.certificationservice.entity.Certification;

public interface CertificationService {

    Certification addCertification(
            CertificationRequestDTO request
    );

    List<Certification> getAllCertifications();

    Certification getCertificationById(UUID id);

    List<Certification> getCertificationsByEmployee(
            UUID employeeId
    );

    Certification updateCertification(
            UUID id,
            CertificationRequestDTO request
    );

    void deleteCertification(UUID id);

    List<Certification> getActiveCertifications();

    List<Certification> getExpiredCertifications();

    List<Certification> getExpiringCertifications(int days);

    Certification renewCertification(
            UUID id,
            CertificationRenewalDTO request
    );

    List<RenewalNotificationDTO> getRenewalNotifications(
            int days
    );

    CertificationReportDTO generateReport();

    ComplianceStatusDTO getComplianceStatus(
            UUID employeeId
    );
}