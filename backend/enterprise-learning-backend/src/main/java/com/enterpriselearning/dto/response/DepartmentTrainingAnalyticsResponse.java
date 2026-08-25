package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTrainingAnalyticsResponse {
    private String department;
    private long totalDepartmentEmployees;
    private long participatingEmployees;
    private double participationRatePercentage;
    
    private long totalEnrollments;
    private long completedEnrollments;
    private long inProgressEnrollments;
    private long notStartedEnrollments;
    
    private double completionPercentage;
    private double averageProgressPercentage;
    private double totalTrainingHoursCompleted;
    
    private Map<String, Long> topCompletedCourses;
}
