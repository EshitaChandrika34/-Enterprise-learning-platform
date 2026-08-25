package com.infosys.learningservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.learningservice.client.CertificationClient;
import com.infosys.learningservice.dto.CertificationRequestDTO;

@RestController
@RequestMapping("/learning-certifications")
public class CertificationIntegrationController {

    private final CertificationClient certificationClient;

    public CertificationIntegrationController(
            CertificationClient certificationClient) {
        this.certificationClient = certificationClient;
    }

    @PostMapping
    public ResponseEntity<Object> createCertification(
            @RequestBody CertificationRequestDTO request) {

        return certificationClient.createCertification(request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllCertifications() {

        return certificationClient.getAllCertifications();
    }
}