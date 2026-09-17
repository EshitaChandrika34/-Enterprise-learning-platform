package com.enterpriselearning.controller;

import com.enterpriselearning.dto.response.AnalyticsDashboardResponse;
import com.enterpriselearning.dto.response.ApiResponse;
import com.enterpriselearning.dto.response.CertificationStatsResponse;
import com.enterpriselearning.dto.response.CourseTrainingAnalyticsResponse;
import com.enterpriselearning.dto.response.DepartmentTrainingAnalyticsResponse;
import com.enterpriselearning.dto.response.EmployeeStatsResponse;
import com.enterpriselearning.dto.response.EmployeeTrainingAnalyticsResponse;
import com.enterpriselearning.dto.response.LearningStatsResponse;
import com.enterpriselearning.dto.response.SkillStatsResponse;
import com.enterpriselearning.dto.response.TrainingAnalyticsResponse;
import com.enterpriselearning.dto.response.TrainingTrendItem;
import com.enterpriselearning.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // ============================================================
    // DASHBOARD ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>>
    getDashboardAnalytics() {

        AnalyticsDashboardResponse response =
                analyticsService.getDashboardAnalytics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // EMPLOYEE ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<EmployeeStatsResponse>>
    getEmployeeStats() {

        EmployeeStatsResponse response =
                analyticsService.getEmployeeStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // SKILL ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/skills")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<SkillStatsResponse>>
    getSkillStats() {

        SkillStatsResponse response =
                analyticsService.getSkillStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Skill analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // LEARNING ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/learning")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<LearningStatsResponse>>
    getLearningStats() {

        LearningStatsResponse response =
                analyticsService.getLearningStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Learning analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // CERTIFICATION ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/certifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CertificationStatsResponse>>
    getCertificationStats() {

        CertificationStatsResponse response =
                analyticsService.getCertificationStats();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Certification analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // OVERALL TRAINING ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<TrainingAnalyticsResponse>>
    getOverallTrainingAnalytics() {

        TrainingAnalyticsResponse response =
                analyticsService.getOverallTrainingAnalytics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Overall training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // CURRENT USER'S OWN TRAINING
    // EMPLOYEE + HR + ADMIN
    // ============================================================

    @GetMapping("/training/my-training")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeTrainingAnalyticsResponse>>
    getMyTrainingAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {

        EmployeeTrainingAnalyticsResponse response =
                analyticsService.getMyTrainingAnalytics(
                        userDetails.getUsername()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Your training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // SPECIFIC EMPLOYEE TRAINING
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<EmployeeTrainingAnalyticsResponse>>
    getEmployeeTrainingAnalytics(
            @PathVariable Long employeeId) {

        EmployeeTrainingAnalyticsResponse response =
                analyticsService.getEmployeeTrainingAnalytics(
                        employeeId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Employee training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // DEPARTMENT TRAINING
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DepartmentTrainingAnalyticsResponse>>
    getDepartmentTrainingAnalytics(
            @PathVariable String department) {

        DepartmentTrainingAnalyticsResponse response =
                analyticsService.getDepartmentTrainingAnalytics(
                        department
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Department training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // ALL DEPARTMENTS TRAINING
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/departments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<DepartmentTrainingAnalyticsResponse>>>
    getAllDepartmentTrainingAnalytics() {

        List<DepartmentTrainingAnalyticsResponse> response =
                analyticsService.getAllDepartmentTrainingAnalytics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All department training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // COURSE TRAINING ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<CourseTrainingAnalyticsResponse>>
    getCourseTrainingAnalytics(
            @PathVariable Long courseId) {

        CourseTrainingAnalyticsResponse response =
                analyticsService.getCourseTrainingAnalytics(
                        courseId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Course training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // ALL COURSES TRAINING ANALYTICS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<CourseTrainingAnalyticsResponse>>>
    getAllCoursesTrainingAnalytics() {

        List<CourseTrainingAnalyticsResponse> response =
                analyticsService.getAllCoursesTrainingAnalytics();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All courses training analytics retrieved successfully",
                        response
                )
        );
    }

    // ============================================================
    // TRAINING TRENDS
    // ADMIN + HR ONLY
    // ============================================================

    @GetMapping("/training/trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<List<TrainingTrendItem>>>
    getTrainingTrends() {

        List<TrainingTrendItem> response =
                analyticsService.getTrainingTrends();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training trends retrieved successfully",
                        response
                )
        );
    }
}