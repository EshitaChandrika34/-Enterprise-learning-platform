package com.infosys.learningservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.learningservice.entity.CourseProgress;

@Repository
public interface CourseProgressRepository
        extends JpaRepository<CourseProgress, Long> {

    List<CourseProgress> findByEnrollmentId(Long enrollmentId);

    Optional<CourseProgress>
    findTopByEnrollmentIdOrderByProgressIdDesc(
            Long enrollmentId);
}