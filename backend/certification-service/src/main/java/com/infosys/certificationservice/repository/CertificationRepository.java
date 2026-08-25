package com.infosys.certificationservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.certificationservice.entity.Certification;

@Repository
public interface CertificationRepository
        extends JpaRepository<Certification, UUID> {

    List<Certification> findByEmployeeId(UUID employeeId);

    List<Certification> findByExpiryDateBefore(LocalDate date);

    List<Certification> findByExpiryDateGreaterThanEqual(LocalDate date);

    List<Certification> findByExpiryDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByCertificateNumber(String certificateNumber);
}