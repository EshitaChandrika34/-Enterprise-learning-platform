package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTrainingAnalyticsResponse {
    private Long courseId;
    private String courseTitle;
    private String category;
    private String level;
    private Integer durationHours;
    private String instructor;
    
    private long totalEnrollments;
    private long completedCount;
    private long inProgressCount;
    private long notStartedCount;
    
    private double completionPercentage;
    private double averageProgressPercentage;
    private double totalTrainingHoursDelivered;
}
