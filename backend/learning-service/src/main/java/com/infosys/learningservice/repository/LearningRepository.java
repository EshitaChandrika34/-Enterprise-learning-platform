package com.infosys.learningservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.learningservice.entity.LearningCourse;

@Repository
public interface LearningRepository extends JpaRepository<LearningCourse, Long> {

}