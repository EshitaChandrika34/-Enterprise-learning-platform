package com.infosys.learningservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.learningservice.entity.Enrollment;

@Repository
public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByEmployeeId(
            Long employeeId
    );
}