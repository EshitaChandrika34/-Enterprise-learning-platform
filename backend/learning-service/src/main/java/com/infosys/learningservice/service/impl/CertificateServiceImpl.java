package com.infosys.learningservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.CertificateDTO;
import com.infosys.learningservice.entity.Certificate;
import com.infosys.learningservice.entity.CourseProgress;
import com.infosys.learningservice.entity.Enrollment;
import com.infosys.learningservice.entity.LearningCourse;
import com.infosys.learningservice.repository.CertificateRepository;
import com.infosys.learningservice.repository.CourseProgressRepository;
import com.infosys.learningservice.repository.EnrollmentRepository;
import com.infosys.learningservice.repository.LearningRepository;
import com.infosys.learningservice.service.CertificateService;

@Service
public class CertificateServiceImpl
        implements CertificateService {

    private static final int CERTIFICATE_THRESHOLD = 80;

    private final CertificateRepository
            certificateRepository;

    private final CourseProgressRepository
            courseProgressRepository;

    private final EnrollmentRepository
            enrollmentRepository;

    private final LearningRepository
            learningRepository;


    public CertificateServiceImpl(
            CertificateRepository certificateRepository,
            CourseProgressRepository courseProgressRepository,
            EnrollmentRepository enrollmentRepository,
            LearningRepository learningRepository) {

        this.certificateRepository =
                certificateRepository;

        this.courseProgressRepository =
                courseProgressRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.learningRepository =
                learningRepository;
    }


    // ==========================================
    // GENERATE CERTIFICATE
    // ==========================================

    @Override
    public CertificateDTO generateCertificate(
            CertificateDTO certificateDTO) {

        if (
                certificateDTO == null
                ||
                certificateDTO.getEnrollmentId() == null
        ) {

            throw new IllegalArgumentException(
                    "Enrollment ID is required"
            );
        }


        Long enrollmentId =
                certificateDTO.getEnrollmentId();


        // ------------------------------------------
        // CHECK ENROLLMENT
        // ------------------------------------------

        Enrollment enrollment =
                enrollmentRepository
                        .findById(enrollmentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Enrollment not found with id: "
                                                + enrollmentId
                                )
                        );


        // ------------------------------------------
        // GET ALL PROGRESS RECORDS
        // ------------------------------------------

        List<CourseProgress> progressList =
                courseProgressRepository
                        .findByEnrollmentId(
                                enrollmentId
                        );


        if (
                progressList == null
                ||
                progressList.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Progress information not found for enrollment: "
                            + enrollmentId
            );
        }


        // ------------------------------------------
        // FIND HIGHEST PROGRESS
        // ------------------------------------------

        int percentage =
                progressList
                        .stream()
                        .map(
                                CourseProgress
                                        ::getProgressPercentage
                        )
                        .filter(
                                value ->
                                        value != null
                        )
                        .max(
                                Integer::compareTo
                        )
                        .orElse(0);


        System.out.println(
                "Certificate check - Enrollment ID: "
                        + enrollmentId
        );

        System.out.println(
                "Certificate check - Progress: "
                        + percentage
                        + "%"
        );


        // ------------------------------------------
        // CHECK 80% THRESHOLD
        // ------------------------------------------

        if (
                percentage
                <
                CERTIFICATE_THRESHOLD
        ) {

            throw new IllegalArgumentException(
                    "Certificate cannot be generated. "
                            + "Minimum "
                            + CERTIFICATE_THRESHOLD
                            + "% progress is required. "
                            + "Current progress is "
                            + percentage
                            + "%."
            );
        }


        // ------------------------------------------
        // PREVENT DUPLICATE CERTIFICATE
        // ------------------------------------------

        if (
                certificateRepository
                        .existsByEnrollmentId(
                                enrollmentId
                        )
        ) {

            throw new IllegalArgumentException(
                    "Certificate already generated "
                            + "for this enrollment."
            );
        }


        // ------------------------------------------
        // GET COURSE
        // ------------------------------------------

        LearningCourse course =
                learningRepository
                        .findById(
                                enrollment
                                        .getCourseId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course not found with id: "
                                                + enrollment
                                                .getCourseId()
                                )
                        );


        // ------------------------------------------
        // CREATE CERTIFICATE
        // ------------------------------------------

        Certificate certificate =
                new Certificate();


        certificate.setEnrollmentId(
                enrollmentId
        );


        certificate.setCourseName(
                course.getCourseName()
        );


        certificate.setCertificateNumber(
                generateCertificateNumber()
        );


        certificate.setIssueDate(
                LocalDate.now()
        );


        certificate.setStatus(
                "ISSUED"
        );


        Certificate savedCertificate =
                certificateRepository
                        .save(
                                certificate
                        );


        return convertToDTO(
                savedCertificate
        );
    }


    // ==========================================
    // CHECK CERTIFICATE ELIGIBILITY
    // ==========================================

    @Override
    public boolean isEligibleForCertificate(
            Long enrollmentId) {

        if (
                enrollmentId == null
        ) {

            return false;
        }


        if (
                !enrollmentRepository
                        .existsById(
                                enrollmentId
                        )
        ) {

            return false;
        }


        // ------------------------------------------
        // GET ALL PROGRESS FOR ENROLLMENT
        // ------------------------------------------

        List<CourseProgress> progressList =
                courseProgressRepository
                        .findByEnrollmentId(
                                enrollmentId
                        );


        if (
                progressList == null
                ||
                progressList.isEmpty()
        ) {

            return false;
        }


        // ------------------------------------------
        // FIND HIGHEST PROGRESS
        // ------------------------------------------

        int percentage =
                progressList
                        .stream()
                        .map(
                                CourseProgress
                                        ::getProgressPercentage
                        )
                        .filter(
                                value ->
                                        value != null
                        )
                        .max(
                                Integer::compareTo
                        )
                        .orElse(0);


        System.out.println(
                "Eligibility check - Enrollment ID: "
                        + enrollmentId
        );


        System.out.println(
                "Eligibility check - Highest Progress: "
                        + percentage
                        + "%"
        );


        return (
                percentage
                >=
                CERTIFICATE_THRESHOLD
        );
    }


    // ==========================================
    // GET ALL CERTIFICATES
    // ==========================================

    @Override
    public List<CertificateDTO>
    getAllCertificates() {

        return certificateRepository
                .findAll()
                .stream()
                .map(
                        this::convertToDTO
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // GET CERTIFICATE BY ID
    // ==========================================

    @Override
    public CertificateDTO
    getCertificateById(
            Long id) {

        Certificate certificate =
                certificateRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Certificate not found with id: "
                                                + id
                                )
                        );


        return convertToDTO(
                certificate
        );
    }


    // ==========================================
    // GET CERTIFICATE BY ENROLLMENT
    // ==========================================

    @Override
    public List<CertificateDTO>
    getCertificatesByEnrollmentId(
            Long enrollmentId) {

        return certificateRepository
                .findByEnrollmentId(
                        enrollmentId
                )
                .stream()
                .map(
                        this::convertToDTO
                )
                .collect(
                        Collectors.toList()
                );
    }


    // ==========================================
    // UPDATE CERTIFICATE
    // ==========================================

    @Override
    public CertificateDTO
    updateCertificate(
            Long id,
            CertificateDTO certificateDTO) {

        Certificate certificate =
                certificateRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Certificate not found with id: "
                                                + id
                                )
                        );


        if (
                certificateDTO
                        .getCourseName()
                != null
        ) {

            certificate.setCourseName(
                    certificateDTO
                            .getCourseName()
            );
        }


        if (
                certificateDTO
                        .getIssueDate()
                != null
        ) {

            certificate.setIssueDate(
                    certificateDTO
                            .getIssueDate()
            );
        }


        if (
                certificateDTO
                        .getStatus()
                != null
                &&
                !certificateDTO
                        .getStatus()
                        .isBlank()
        ) {

            certificate.setStatus(
                    certificateDTO
                            .getStatus()
            );
        }


        Certificate updatedCertificate =
                certificateRepository
                        .save(
                                certificate
                        );


        return convertToDTO(
                updatedCertificate
        );
    }


    // ==========================================
    // DELETE CERTIFICATE
    // ==========================================

    @Override
    public void deleteCertificate(
            Long id) {

        Certificate certificate =
                certificateRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Certificate not found with id: "
                                                + id
                                )
                        );


        certificateRepository
                .delete(
                        certificate
                );
    }


    // ==========================================
    // GENERATE CERTIFICATE NUMBER
    // ==========================================

    private String generateCertificateNumber() {

        return "CERT-"
                +
                UUID
                        .randomUUID()
                        .toString()
                        .substring(
                                0,
                                8
                        )
                        .toUpperCase();
    }


    // ==========================================
    // ENTITY TO DTO
    // ==========================================

    private CertificateDTO
    convertToDTO(
            Certificate certificate) {

        CertificateDTO dto =
                new CertificateDTO();


        dto.setCertificateId(
                certificate
                        .getCertificateId()
        );


        dto.setEnrollmentId(
                certificate
                        .getEnrollmentId()
        );


        dto.setCertificateNumber(
                certificate
                        .getCertificateNumber()
        );


        dto.setCourseName(
                certificate
                        .getCourseName()
        );


        dto.setIssueDate(
                certificate
                        .getIssueDate()
        );


        dto.setStatus(
                certificate
                        .getStatus()
        );


        return dto;
    }
}