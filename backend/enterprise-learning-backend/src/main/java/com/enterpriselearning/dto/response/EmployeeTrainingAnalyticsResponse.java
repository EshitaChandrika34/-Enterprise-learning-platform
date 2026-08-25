package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrainingAnalyticsResponse {
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String employeeEmail;
    private String department;
    
    private long totalEnrolledCourses;
    private long completedCourses;
    private long inProgressCourses;
    private long notStartedCourses;
    
    private double completionPercentage;
    private double averageProgressPercentage;
    private double totalTrainingHoursCompleted;
    
    private List<EmployeeCourseProgressDetail> courseProgressDetails;
}
