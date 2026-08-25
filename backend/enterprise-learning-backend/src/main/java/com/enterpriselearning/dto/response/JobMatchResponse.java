package com.enterpriselearning.dto.response;

import com.enterpriselearning.entity.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatchResponse {
    private Long jobId;
    private String jobTitle;
    private String jobDepartment;
    private String jobLocation;
    private String employmentType;
    private Integer requiredExperienceYears;
    
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    
    private double overallMatchScore; // 0 to 100%
    private String compatibilityLevel; // EXCELLENT (>=85%), GOOD (70-84%), MODERATE (50-69%), LOW (<50%)
    
    // Component Scores
    private double skillScore; // Max 60%
    private double experienceScore; // Max 25%
    private double certificationsScore; // Max 15%
    
    // Detailed Breakdown
    private List<SkillMatchDetail> skillDetails;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    
    private Integer currentExperienceYears;
    private boolean experienceSatisfied;
    
    // Gap Analysis & Learning Recommendations
    private List<String> gapAnalysis;
    private List<CourseResponse> recommendedCoursesToBridgeGap;
    
    // Application Info
    private boolean alreadyApplied;
    private JobApplicationStatus applicationStatus;
}
