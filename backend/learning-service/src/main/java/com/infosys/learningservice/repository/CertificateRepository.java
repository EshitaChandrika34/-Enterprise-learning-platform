package com.infosys.learningservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.learningservice.entity.Certificate;

@Repository
public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    List<Certificate> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);
}