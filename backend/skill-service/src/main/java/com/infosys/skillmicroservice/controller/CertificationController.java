package com.infosys.skillmicroservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.skillmicroservice.entity.Certification;
import com.infosys.skillmicroservice.service.CertificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(
            CertificationService certificationService) {

        this.certificationService = certificationService;
    }

    @PostMapping
    public Certification addCertification(
            @Valid @RequestBody Certification certification) {

        return certificationService
                .addCertification(certification);
    }

    @GetMapping
    public List<Certification> getAllCertifications() {
        return certificationService
                .getAllCertifications();
    }

    @GetMapping("/{certificationId}")
    public Certification getCertificationById(
            @PathVariable UUID certificationId) {

        return certificationService
                .getCertificationById(certificationId);
    }

    @GetMapping("/user/{userId}")
    public List<Certification> getCertificationsByUserId(
            @PathVariable UUID userId) {

        return certificationService
                .getCertificationsByUserId(userId);
    }

    @PutMapping("/{certificationId}")
    public Certification updateCertification(
            @PathVariable UUID certificationId,
            @Valid @RequestBody Certification certification) {

        return certificationService.updateCertification(
                certificationId,
                certification);
    }

    @DeleteMapping("/{certificationId}")
    public String deleteCertification(
            @PathVariable UUID certificationId) {

        certificationService
                .deleteCertification(certificationId);

        return "Certification deleted successfully";
    }
}