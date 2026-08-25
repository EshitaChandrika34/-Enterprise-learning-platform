package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerProgressResponse {
    private Long careerGoalId;
    private String targetRole;
    private Long careerPathId;
    private String careerPathTitle;
    private String department;
    
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    
    // Overall multi-factor percentage (0.0 to 100.0%)
    private double overallProgressPercentage;
    private String readinessStage; // READY_FOR_PROMOTION, NEAR_READINESS, DEVELOPING, EARLY_STAGE
    
    // 1. Skill Mastery Factor (40% Weight)
    private double skillProgressPercentage;
    private double skillContributionScore; // Max 40.0 pts
    private int skillsMatchedCount;
    private int skillsTotalCount;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    
    // 2. Learning & Course Progress Factor (30% Weight)
    private double courseProgressPercentage;
    private double courseContributionScore; // Max 30.0 pts
    private int coursesCompletedCount;
    private int coursesTotalCount;
    private List<String> completedCourses;
    private List<String> pendingCourses;
    
    // 3. Certification Factor (15% Weight)
    private double certificationProgressPercentage;
    private double certificationContributionScore; // Max 15.0 pts
    private int certificationsCount;
    
    // 4. Experience & Tenure Factor (15% Weight)
    private double experienceProgressPercentage;
    private double experienceContributionScore; // Max 15.0 pts
    private int currentExperienceYears;
    private int requiredExperienceYears;
    
    // Actionable Milestones & Guidance
    private List<String> actionableMilestones;
    private LocalDate targetDate;
    private String goalStatus;
}
