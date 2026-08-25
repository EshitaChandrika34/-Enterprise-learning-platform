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
public class LearningStatsResponse {
    private long totalCourses;
    private long totalEnrollments;
    private long completedEnrollments;
    private long inProgressEnrollments;
    private double averageProgressPercentage;
    private Map<String, Long> topEnrolledCourses;
}
