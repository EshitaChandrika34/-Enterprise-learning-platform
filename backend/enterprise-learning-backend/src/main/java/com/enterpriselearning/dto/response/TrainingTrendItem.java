package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTrendItem {
    private String period; // e.g. "2026-08"
    private long newEnrollments;
    private long completedCourses;
    private double trainingHoursCompleted;
}
