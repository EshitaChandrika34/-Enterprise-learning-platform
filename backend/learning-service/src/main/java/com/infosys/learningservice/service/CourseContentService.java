package com.infosys.learningservice.service;

import java.util.List;

import com.infosys.learningservice.dto.CourseContentDTO;

public interface CourseContentService {

    CourseContentDTO addCourseContent(
            CourseContentDTO courseContentDTO);

    List<CourseContentDTO> getAllCourseContents();

    CourseContentDTO getCourseContentById(Long id);

    List<CourseContentDTO> getCourseContentsByCourseId(
            Long courseId);

    CourseContentDTO updateCourseContent(
            Long id,
            CourseContentDTO courseContentDTO);

    void deleteCourseContent(Long id);
}