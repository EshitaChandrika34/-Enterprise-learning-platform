package com.infosys.learningservice.service;

import java.util.List;

import com.infosys.learningservice.dto.LearningCourseDTO;

public interface LearningService {

    LearningCourseDTO addCourse(LearningCourseDTO courseDTO);

    List<LearningCourseDTO> getAllCourses();

    LearningCourseDTO getCourseById(Long courseId);

    LearningCourseDTO updateCourse(Long courseId, LearningCourseDTO courseDTO);

    void deleteCourse(Long courseId);

}