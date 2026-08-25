package com.infosys.learningservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.EnrollmentDTO;
import com.infosys.learningservice.entity.Enrollment;
import com.infosys.learningservice.repository.EnrollmentRepository;
import com.infosys.learningservice.service.EnrollmentService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public EnrollmentDTO addEnrollment(EnrollmentDTO dto) {

        Enrollment enrollment = new Enrollment();

        enrollment.setEmployeeId(dto.getEmployeeId());
        enrollment.setCourseId(dto.getCourseId());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());
        enrollment.setStatus(dto.getStatus());

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToDTO(savedEnrollment);
    }

    @Override
    public List<EnrollmentDTO> getAllEnrollments() {

        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found with id: " + id));

        return convertToDTO(enrollment);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByEmployeeId(
            Long employeeId) {

        return enrollmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentDTO updateEnrollment(
            Long id, EnrollmentDTO dto) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found with id: " + id));

        enrollment.setEmployeeId(dto.getEmployeeId());
        enrollment.setCourseId(dto.getCourseId());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());
        enrollment.setStatus(dto.getStatus());

        Enrollment updatedEnrollment =
                enrollmentRepository.save(enrollment);

        return convertToDTO(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Enrollment not found with id: " + id));

        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentDTO convertToDTO(Enrollment enrollment) {

        return new EnrollmentDTO(
                enrollment.getEnrollmentId(),
                enrollment.getEmployeeId(),
                enrollment.getCourseId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus()
        );
    }
}