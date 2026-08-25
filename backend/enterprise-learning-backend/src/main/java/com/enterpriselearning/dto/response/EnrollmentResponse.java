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
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private String employeeName;
    private String employeeId;
    private Long courseId;
    private String courseTitle;
    private String courseCategory;
    private String courseLevel;
    private Integer durationHours;
    private EnrollmentStatus status;
    private Integer progressPercentage;
    private LocalDateTime enrolledDate;
    private LocalDateTime completedDate;
    private LocalDateTime lastAccessedDate;
}
