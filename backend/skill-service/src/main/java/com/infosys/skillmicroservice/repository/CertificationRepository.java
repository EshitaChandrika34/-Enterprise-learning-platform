package com.infosys.skillmicroservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.skillmicroservice.entity.Certification;

@Repository
public interface CertificationRepository
        extends JpaRepository<Certification, UUID> {

    List<Certification> findByUserId(UUID userId);
}