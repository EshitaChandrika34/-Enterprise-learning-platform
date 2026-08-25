package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.CertificationRequest;
import com.enterpriselearning.dto.response.CertificationResponse;
import com.enterpriselearning.entity.Certification;
import com.enterpriselearning.entity.User;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.CertificationRepository;
import com.enterpriselearning.repository.UserRepository;
import com.enterpriselearning.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CertificationResponse addCertification(CertificationRequest request, String currentUserEmail) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        } else {
            user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        }

        Certification cert = Certification.builder()
                .title(request.getTitle())
                .issuingOrganization(request.getIssuingOrganization())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
                .credentialId(request.getCredentialId())
                .credentialUrl(request.getCredentialUrl())
                .user(user)
                .build();

        Certification saved = certificationRepository.save(cert);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponse> getAllCertifications() {
        return certificationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CertificationResponse getCertificationById(Long id) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id));
        return mapToResponse(cert);
    }

    @Override
    @Transactional
    public CertificationResponse updateCertification(Long id, CertificationRequest request) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id));

        cert.setTitle(request.getTitle());
        cert.setIssuingOrganization(request.getIssuingOrganization());
        cert.setIssueDate(request.getIssueDate());
        cert.setExpiryDate(request.getExpiryDate());
        cert.setCredentialId(request.getCredentialId());
        cert.setCredentialUrl(request.getCredentialUrl());

        if (request.getUserId() != null && !request.getUserId().equals(cert.getUser().getId())) {
            User newUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            cert.setUser(newUser);
        }

        Certification updated = certificationRepository.save(cert);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCertification(Long id) {
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + id));
        certificationRepository.delete(cert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponse> getCertificationsByEmployee(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + userId);
        }
        return certificationRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponse> getMyCertifications(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        return certificationRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CertificationResponse mapToResponse(Certification c) {
        return CertificationResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .employeeName(c.getUser().getFirstName() + " " + c.getUser().getLastName())
                .employeeId(c.getUser().getEmployeeId())
                .title(c.getTitle())
                .issuingOrganization(c.getIssuingOrganization())
                .issueDate(c.getIssueDate())
                .expiryDate(c.getExpiryDate())
                .credentialId(c.getCredentialId())
                .credentialUrl(c.getCredentialUrl())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
