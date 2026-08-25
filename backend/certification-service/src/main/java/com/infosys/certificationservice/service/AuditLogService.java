package com.infosys.certificationservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.certificationservice.entity.AuditLog;

public interface AuditLogService {

    void createAuditLog(
            UUID certificationId,
            UUID employeeId,
            String action,
            String description
    );

    List<AuditLog> getAllAuditLogs();

    List<AuditLog> getAuditLogsByEmployeeId(
            UUID employeeId
    );

    List<AuditLog> getAuditLogsByCertificationId(
            UUID certificationId
    );
}