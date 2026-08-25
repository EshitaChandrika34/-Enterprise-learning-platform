package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCourseProgressDetail {
    private Long courseId;
    private String courseTitle;
    private String category;
    private String level;
    private Integer durationHours;
    private EnrollmentStatus status;
    private Integer progressPercentage;
    private Double hoursSpent;
    private LocalDateTime enrolledDate;
    private LocalDateTime completedDate;
    private LocalDateTime lastAccessedDate;
}
