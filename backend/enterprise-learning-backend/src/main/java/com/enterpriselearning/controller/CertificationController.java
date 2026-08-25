package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.CertificationRequest;
import com.enterpriselearning.dto.response.ApiResponse;
import com.enterpriselearning.dto.response.CertificationResponse;
import com.enterpriselearning.service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CertificationResponse>> addCertification(
            @Valid @RequestBody CertificationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CertificationResponse response = certificationService.addCertification(request, userDetails != null ? userDetails.getUsername() : null);
        return new ResponseEntity<>(ApiResponse.success("Certification added successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getAllCertifications() {
        List<CertificationResponse> certifications = certificationService.getAllCertifications();
        return ResponseEntity.ok(ApiResponse.success("Certifications retrieved successfully", certifications));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CertificationResponse>> getCertificationById(@PathVariable Long id) {
        CertificationResponse response = certificationService.getCertificationById(id);
        return ResponseEntity.ok(ApiResponse.success("Certification retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<CertificationResponse>> updateCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequest request) {
        CertificationResponse response = certificationService.updateCertification(id, request);
        return ResponseEntity.ok(ApiResponse.success("Certification updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.ok(ApiResponse.success("Certification deleted successfully", null));
    }

    @GetMapping("/employee/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getCertificationsByEmployee(@PathVariable Long userId) {
        List<CertificationResponse> certifications = certificationService.getCertificationsByEmployee(userId);
        return ResponseEntity.ok(ApiResponse.success("Employee certifications retrieved successfully", certifications));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getMyCertifications(@AuthenticationPrincipal UserDetails userDetails) {
        List<CertificationResponse> certifications = certificationService.getMyCertifications(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("My certifications retrieved successfully", certifications));
    }
}
