package com.enterpriselearning.service;

import com.enterpriselearning.dto.response.*;

import java.util.List;

public interface AnalyticsService {
    AnalyticsDashboardResponse getDashboardAnalytics();
    EmployeeStatsResponse getEmployeeStats();
    SkillStatsResponse getSkillStats();
    LearningStatsResponse getLearningStats();
    CertificationStatsResponse getCertificationStats();

    // Advanced Training Analytics
    TrainingAnalyticsResponse getOverallTrainingAnalytics();
    EmployeeTrainingAnalyticsResponse getEmployeeTrainingAnalytics(Long employeeId);
    EmployeeTrainingAnalyticsResponse getMyTrainingAnalytics(String userEmail);
    DepartmentTrainingAnalyticsResponse getDepartmentTrainingAnalytics(String department);
    List<DepartmentTrainingAnalyticsResponse> getAllDepartmentTrainingAnalytics();
    CourseTrainingAnalyticsResponse getCourseTrainingAnalytics(Long courseId);
    List<CourseTrainingAnalyticsResponse> getAllCoursesTrainingAnalytics();
    List<TrainingTrendItem> getTrainingTrends();
}
