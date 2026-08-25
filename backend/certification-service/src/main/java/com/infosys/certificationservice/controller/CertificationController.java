package com.infosys.certificationservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.certificationservice.dto.CertificationRenewalDTO;
import com.infosys.certificationservice.dto.CertificationReportDTO;
import com.infosys.certificationservice.dto.CertificationRequestDTO;
import com.infosys.certificationservice.dto.ComplianceStatusDTO;
import com.infosys.certificationservice.dto.RenewalNotificationDTO;
import com.infosys.certificationservice.entity.Certification;
import com.infosys.certificationservice.service.CertificationService;
import jakarta.validation.Valid;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(
            CertificationService certificationService) {

        this.certificationService = certificationService;
    }

    @PostMapping
    public ResponseEntity<Certification> addCertification(
            @Valid @RequestBody
            CertificationRequestDTO request) {

        Certification certification =
                certificationService
                        .addCertification(request);

        return new ResponseEntity<>(
                certification,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Certification>>
    getAllCertifications() {

        return ResponseEntity.ok(
                certificationService
                        .getAllCertifications()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<Certification>>
    getActiveCertifications() {

        return ResponseEntity.ok(
                certificationService
                        .getActiveCertifications()
        );
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Certification>>
    getExpiredCertifications() {

        return ResponseEntity.ok(
                certificationService
                        .getExpiredCertifications()
        );
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Certification>>
    getExpiringCertifications(
            @RequestParam(defaultValue = "30")
            int days) {

        return ResponseEntity.ok(
                certificationService
                        .getExpiringCertifications(days)
        );
    }

    @GetMapping("/renewal-notifications")
    public ResponseEntity<List<RenewalNotificationDTO>>
    getRenewalNotifications(
            @RequestParam(defaultValue = "30")
            int days) {

        return ResponseEntity.ok(
                certificationService
                        .getRenewalNotifications(days)
        );
    }

    @GetMapping("/report")
    public ResponseEntity<CertificationReportDTO>
    generateReport() {

        return ResponseEntity.ok(
                certificationService.generateReport()
        );
    }

    @GetMapping("/compliance/{employeeId}")
    public ResponseEntity<ComplianceStatusDTO>
    getComplianceStatus(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(
                certificationService
                        .getComplianceStatus(employeeId)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Certification>>
    getCertificationsByEmployee(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(
                certificationService
                        .getCertificationsByEmployee(
                                employeeId
                        )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certification>
    getCertificationById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                certificationService
                        .getCertificationById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certification>
    updateCertification(
            @PathVariable UUID id,
            @Valid @RequestBody
            CertificationRequestDTO request) {

        return ResponseEntity.ok(
                certificationService
                        .updateCertification(id, request)
        );
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<Certification>
    renewCertification(
            @PathVariable UUID id,
            @Valid @RequestBody
            CertificationRenewalDTO request) {

        return ResponseEntity.ok(
                certificationService
                        .renewCertification(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteCertification(
            @PathVariable UUID id) {

        certificationService
                .deleteCertification(id);

        return ResponseEntity.ok(
                "Certification deleted successfully"
        );
    }
}