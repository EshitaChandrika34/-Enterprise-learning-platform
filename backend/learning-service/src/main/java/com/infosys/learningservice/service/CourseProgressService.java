package com.infosys.learningservice.service;

import java.util.List;

import com.infosys.learningservice.dto.CourseProgressDTO;

public interface CourseProgressService {

    CourseProgressDTO addProgress(CourseProgressDTO dto);

    List<CourseProgressDTO> getAllProgress();

    CourseProgressDTO getProgressById(Long id);

    List<CourseProgressDTO> getProgressByEnrollmentId(Long enrollmentId);

    CourseProgressDTO updateProgress(Long id, CourseProgressDTO dto);

    void deleteProgress(Long id);
}