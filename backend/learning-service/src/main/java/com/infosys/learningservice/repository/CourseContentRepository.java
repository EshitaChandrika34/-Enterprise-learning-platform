package com.infosys.learningservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.learningservice.entity.CourseContent;

@Repository
public interface CourseContentRepository
        extends JpaRepository<CourseContent, Long> {

    List<CourseContent> findByCourseId(Long courseId);
}