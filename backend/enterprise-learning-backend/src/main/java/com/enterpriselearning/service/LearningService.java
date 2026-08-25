package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.CourseRequest;
import com.enterpriselearning.dto.request.EnrollmentRequest;
import com.enterpriselearning.dto.request.ProgressUpdateRequest;
import com.enterpriselearning.dto.response.CourseResponse;
import com.enterpriselearning.dto.response.EnrollmentResponse;

import java.util.List;

public interface LearningService {
    CourseResponse createCourse(CourseRequest request);
    List<CourseResponse> getAllCourses(String category, String level, String search);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);

    EnrollmentResponse enrollInCourse(EnrollmentRequest request, String currentUserEmail);
    EnrollmentResponse updateProgress(Long enrollmentId, ProgressUpdateRequest request, String currentUserEmail);
    List<EnrollmentResponse> getEnrollmentsByEmployee(Long userId);
    List<EnrollmentResponse> getMyEnrollments(String currentUserEmail);
    EnrollmentResponse getEnrollmentById(Long enrollmentId);
}
