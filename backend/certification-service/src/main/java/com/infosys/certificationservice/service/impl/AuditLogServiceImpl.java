package com.infosys.certificationservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.certificationservice.entity.AuditLog;
import com.infosys.certificationservice.repository.AuditLogRepository;
import com.infosys.certificationservice.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void createAuditLog(
            UUID certificationId,
            UUID employeeId,
            String action,
            String description) {

        AuditLog auditLog = new AuditLog(
                certificationId,
                employeeId,
                action,
                description
        );

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getAuditLogsByEmployeeId(
            UUID employeeId) {

        return auditLogRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<AuditLog> getAuditLogsByCertificationId(
            UUID certificationId) {

        return auditLogRepository
                .findByCertificationId(certificationId);
    }
}