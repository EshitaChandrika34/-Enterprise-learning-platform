package com.enterpriselearning.controller;

import com.enterpriselearning.dto.request.CourseRequest;
import com.enterpriselearning.dto.request.EnrollmentRequest;
import com.enterpriselearning.dto.request.ProgressUpdateRequest;
import com.enterpriselearning.dto.response.ApiResponse;
import com.enterpriselearning.dto.response.CourseResponse;
import com.enterpriselearning.dto.response.EnrollmentResponse;
import com.enterpriselearning.service.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = learningService.createCourse(request);
        return new ResponseEntity<>(ApiResponse.success("Course created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String search) {
        List<CourseResponse> courses = learningService.getAllCourses(category, level, search);
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        CourseResponse response = learningService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", response));
    }

    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        CourseResponse response = learningService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", response));
    }

    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        learningService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollInCourse(
            @Valid @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnrollmentResponse response = learningService.enrollInCourse(request, userDetails != null ? userDetails.getUsername() : null);
        return new ResponseEntity<>(ApiResponse.success("Enrolled successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/progress/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateProgress(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody ProgressUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnrollmentResponse response = learningService.updateProgress(enrollmentId, request, userDetails != null ? userDetails.getUsername() : null);
        return ResponseEntity.ok(ApiResponse.success("Learning progress updated successfully", response));
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyCourses(@AuthenticationPrincipal UserDetails userDetails) {
        List<EnrollmentResponse> enrollments = learningService.getMyEnrollments(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("My courses retrieved successfully", enrollments));
    }

    @GetMapping("/employee/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByEmployee(@PathVariable Long userId) {
        List<EnrollmentResponse> enrollments = learningService.getEnrollmentsByEmployee(userId);
        return ResponseEntity.ok(ApiResponse.success("Employee enrollments retrieved successfully", enrollments));
    }

    @GetMapping("/enrollments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse response = learningService.getEnrollmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment retrieved successfully", response));
    }
}
