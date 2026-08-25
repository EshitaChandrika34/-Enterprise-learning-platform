package com.infosys.learningservice.service;

import java.util.List;

import com.infosys.learningservice.dto.EnrollmentDTO;

public interface EnrollmentService {

    EnrollmentDTO addEnrollment(
            EnrollmentDTO dto
    );

    List<EnrollmentDTO> getAllEnrollments();

    EnrollmentDTO getEnrollmentById(
            Long id
    );

    List<EnrollmentDTO> getEnrollmentsByEmployeeId(
            Long employeeId
    );

    EnrollmentDTO updateEnrollment(
            Long id,
            EnrollmentDTO dto
    );

    void deleteEnrollment(
            Long id
    );
}