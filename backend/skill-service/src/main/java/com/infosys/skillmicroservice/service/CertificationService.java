package com.infosys.skillmicroservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.skillmicroservice.entity.Certification;

public interface CertificationService {

    Certification addCertification(Certification certification);

    List<Certification> getAllCertifications();

    Certification getCertificationById(UUID certificationId);

    List<Certification> getCertificationsByUserId(UUID userId);

    Certification updateCertification(
            UUID certificationId,
            Certification certification);

    void deleteCertification(UUID certificationId);
}