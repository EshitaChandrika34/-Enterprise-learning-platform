package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAnalyticsResponse {
    // Overall Stats
    private long totalCourses;
    private long totalEnrollments;
    private long completedEnrollments;
    private long inProgressEnrollments;
    private long notStartedEnrollments;
    
    private double overallCompletionPercentage;
    private double averageTrainingProgressPercentage;
    private double totalTrainingHoursCompleted;
    
    private long totalLearnersParticipating;
    private double enterpriseTrainingParticipationRate;
    
    // Breakdowns
    private Map<String, Long> enrollmentsByCategory;
    private List<DepartmentTrainingAnalyticsResponse> departmentAnalytics;
    private List<CourseTrainingAnalyticsResponse> topPerformingCourses;
    private List<TrainingTrendItem> monthlyTrends;
}
