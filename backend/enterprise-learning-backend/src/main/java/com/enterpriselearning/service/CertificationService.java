package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.CertificationRequest;
import com.enterpriselearning.dto.response.CertificationResponse;

import java.util.List;

public interface CertificationService {
    CertificationResponse addCertification(CertificationRequest request, String currentUserEmail);
    List<CertificationResponse> getAllCertifications();
    CertificationResponse getCertificationById(Long id);
    CertificationResponse updateCertification(Long id, CertificationRequest request);
    void deleteCertification(Long id);
    List<CertificationResponse> getCertificationsByEmployee(Long userId);
    List<CertificationResponse> getMyCertifications(String currentUserEmail);
}
