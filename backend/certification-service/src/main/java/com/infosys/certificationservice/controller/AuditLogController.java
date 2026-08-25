package com.infosys.certificationservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.certificationservice.entity.AuditLog;
import com.infosys.certificationservice.service.AuditLogService;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService) {

        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>>
    getAllAuditLogs() {

        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs()
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AuditLog>>
    getByEmployeeId(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByEmployeeId(
                                employeeId
                        )
        );
    }

    @GetMapping("/certification/{certificationId}")
    public ResponseEntity<List<AuditLog>>
    getByCertificationId(
            @PathVariable UUID certificationId) {

        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogsByCertificationId(
                                certificationId
                        )
        );
    }
}