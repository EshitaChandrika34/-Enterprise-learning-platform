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
public class PromotionEvaluationResponse {
    private Long criteriaId;
    private String criteriaName;
    private String targetRole;
    private Integer minimumRequiredScore;
    
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String department;
    
    private boolean eligible;
    private double overallScore; // 0 to 100
    
    // Skills evaluation (40%)
    private boolean skillsSatisfied;
    private double skillsScore;
    private List<String> requiredSkills;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    
    // Experience evaluation (25%)
    private boolean experienceSatisfied;
    private double experienceScore;
    private Integer currentExperienceYears;
    private Integer requiredExperienceYears;
    
    // Course completion evaluation (20%)
    private boolean coursesSatisfied;
    private double coursesScore;
    private long completedCoursesCount;
    private Integer requiredCompletedCourses;
    
    // Certifications evaluation (15%)
    private boolean certificationsSatisfied;
    private double certificationsScore;
    private long certificationsCount;
    private Integer requiredCertifications;
    
    // Department check
    private boolean departmentSatisfied;
    private String requiredDepartment;
    
    // Actionable Feedback
    private List<String> feedback;
}
