package com.infosys.learningservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.learningservice.dto.CertificateDTO;
import com.infosys.learningservice.service.CertificateService;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(
            CertificateService certificateService) {

        this.certificateService = certificateService;
    }


    // ==========================================
    // GENERATE CERTIFICATE
    // ==========================================

    @PostMapping
    public ResponseEntity<CertificateDTO> generateCertificate(
            @RequestBody CertificateDTO certificateDTO) {

        CertificateDTO savedCertificate =
                certificateService.generateCertificate(
                        certificateDTO);

        return new ResponseEntity<>(
                savedCertificate,
                HttpStatus.CREATED
        );
    }


    // ==========================================
    // CHECK CERTIFICATE ELIGIBILITY
    // ==========================================

    @GetMapping("/eligibility/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> checkEligibility(
            @PathVariable Long enrollmentId) {

        boolean eligible =
                certificateService
                        .isEligibleForCertificate(
                                enrollmentId
                        );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "enrollmentId",
                enrollmentId
        );

        response.put(
                "threshold",
                80
        );

        response.put(
                "eligible",
                eligible
        );

        if (eligible) {

            response.put(
                    "message",
                    "Employee is eligible for certificate generation."
            );

        } else {

            response.put(
                    "message",
                    "Minimum 80% progress is required."
            );
        }

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // GET ALL CERTIFICATES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<CertificateDTO>>
    getAllCertificates() {

        return ResponseEntity.ok(
                certificateService
                        .getAllCertificates()
        );
    }


    // ==========================================
    // GET CERTIFICATE BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO>
    getCertificateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                certificateService
                        .getCertificateById(id)
        );
    }


    // ==========================================
    // GET CERTIFICATES BY ENROLLMENT
    // ==========================================

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<CertificateDTO>>
    getCertificatesByEnrollmentId(
            @PathVariable Long enrollmentId) {

        return ResponseEntity.ok(
                certificateService
                        .getCertificatesByEnrollmentId(
                                enrollmentId
                        )
        );
    }


    // ==========================================
    // UPDATE CERTIFICATE
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<CertificateDTO>
    updateCertificate(
            @PathVariable Long id,
            @RequestBody CertificateDTO certificateDTO) {

        return ResponseEntity.ok(
                certificateService
                        .updateCertificate(
                                id,
                                certificateDTO
                        )
        );
    }


    // ==========================================
    // DELETE CERTIFICATE
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteCertificate(
            @PathVariable Long id) {

        certificateService
                .deleteCertificate(id);

        return ResponseEntity.ok(
                "Certificate deleted successfully."
        );
    }
}